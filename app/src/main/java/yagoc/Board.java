package yagoc;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceType;
import yagoc.pieces.Pieces;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static yagoc.Square.castlingKingsideBlackFrom;
import static yagoc.Square.castlingKingsideBlackTo;
import static yagoc.Square.castlingKingsideWhiteFrom;
import static yagoc.Square.castlingKingsideWhiteTo;
import static yagoc.Square.castlingQueensideBlackFrom;
import static yagoc.Square.castlingQueensideBlackTo;
import static yagoc.Square.castlingQueensideWhiteFrom;
import static yagoc.Square.castlingQueensideWhiteTo;
import static yagoc.Yagoc.logger;
import static yagoc.pieces.PieceType.King;
import static yagoc.pieces.PieceType.Pawn;

// 1 pawn
// 2 knight
// 3 bishop
// 4 rook
// 5 queen
// 6 king
/*
0        -4, -2, -3, -5, -6, -3, -2, -4,
1        -1, -1, -1, -1, -1, -1, -1, -1,
2         0,  0,  0,  0,  0,  0,  0,  0,
3         0,  0,  0,  0,  0,  0,  0,  0,
4         0,  0,  0,  0,  0,  0,  0,  0,
5         0,  0,  0,  0,  0,  0,  0,  0,
6         1,  1,  1,  1,  1,  1,  1,  1,
7         4,  2,  3,  5,  6,  3,  2,  4
*/
public class Board implements Serializable {
    private Piece[][] squares;
    private int[] enPassant;
    private Player currentPlayer;
    private boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
    private boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
    private boolean finished;
    private int drawCounter;
    private int moveCounter;
    private Player blackPlayer, whitePlayer;

    public int enPassant(int file) {
        return enPassant[file];
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public boolean hasWhiteLeftRookMoved() {
        return whiteLeftRookMoved;
    }

    public boolean hasWhiteRightRookMoved() {
        return whiteRightRookMoved;
    }

    public boolean hasWhiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean hasBlackLeftRookMoved() {
        return blackLeftRookMoved;
    }

    public boolean hasBlackRightRookMoved() {
        return blackRightRookMoved;
    }

    public boolean hasBlackKingMoved() {
        return blackKingMoved;
    }

    public int drawCounter() {
        return drawCounter;
    }

    public int moveCounter() {
        return moveCounter;
    }

    public Player blackPlayer() {
        return blackPlayer;
    }

    public Player whitePlayer() {
        return whitePlayer;
    }

    public Board() {
        reset();
    }

    public Board copy() {
        Board board = new Board();
        board.resetWith(this);
        return board;
    }

    private Piece[][] newTable() {
        Piece[][] pieces = new Piece[8][8];

        for (Piece[] piece : pieces) {
            Arrays.fill(piece, Pieces.none);
        }

        return pieces;
    }

    void movePlayer(Player player) {
        if (player.isComputer()) {
            Move move = player.move(this);

            play(move);
            logger.info(move.toString());

            ifPawnHasReachedFinalRankReplaceWithQueen(move);
        }
    }

    private void ifPawnHasReachedFinalRankReplaceWithQueen(Move move) {
        //TODO What if there is already a queen?
        if ((move.fromPiece() == Pieces.blackPawn && move.to().rank() == 7)) {
            pieceAt(move.to(), Pieces.blackQueen);
        } else if (move.fromPiece() == Pieces.whitePawn && move.to().rank() == 0) {
            pieceAt(move.to(), Pieces.whiteQueen);
        }

        finished = noMoreMovesAllowed();
    }

    boolean isPieceOfCurrentPlayer(Piece piece) {
        return piece.color() == currentPlayer.pieceColor();
    }

    public boolean isPieceOfOppositePlayer(Piece piece) {
        return piece != Pieces.none && piece.color() != currentPlayer.pieceColor();
    }

    boolean moveIfPossible(Square from, Square to) {
        Move move = new Move(pieceAt(from), from, to);
        if (finished || !isPieceOfCurrentPlayer(move.fromPiece())) {
            return false;
        } else if ((currentPlayer == blackPlayer && blackPlayer.isUser()) || (currentPlayer == whitePlayer && whitePlayer.isUser())) {
            if (!from.equals(to)
                    && isCorrectMove(move)
                    && moveDoesNotCreateCheck(move)) {

                play(move);
                if ((move.fromPiece() == Pieces.blackPawn && to.rank() == 7) || (move.fromPiece() == Pieces.whitePawn && to.rank() == 0)) {
                    // TODO Should be able to choose piece instead of always getting a Queen
                    pieceAt(to, move.fromPiece().switchTo(PieceType.Queen));
                }

                finished = noMoreMovesAllowed();

                logger.info(move.toString());

                return true;
            }
        }

        return false;
    }

    MoveLog play(Move move) {
        MoveLog moveLog = new MoveLog(this, move);

        if (move.fromPiece() == Pieces.whiteKing)
            whiteKingMoved = true;
        else if (move.fromPiece() == Pieces.whiteRook && move.from().file() == 0)
            whiteLeftRookMoved = true;
        else if (move.fromPiece() == Pieces.whiteRook && move.from().file() == 7)
            whiteRightRookMoved = true;

        if (move.fromPiece() == Pieces.blackKing)
            blackKingMoved = true;
        else if (move.fromPiece() == Pieces.blackRook && move.from().file() == 0)
            blackLeftRookMoved = true;
        else if (move.fromPiece() == Pieces.blackRook && move.from().file() == 7)
            blackRightRookMoved = true;

        if (move.isCastling()) {
            drawCounter++;
            moveLog.type = MoveType.castling;
            playCastlingExtraMove(moveLog);
        } else {
            if (pieceAt(move.to()) != Pieces.none || move.fromPiece().pieceType() == Pawn) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else {
                drawCounter++;
            }
            //si peon avanza dos activar posible captura al paso
            if (move.fromPiece().pieceType() == Pawn && move.rankDistanceAbs() == 2) {
                enPassant[move.to().file()] = moveCounter;
            }
            //realizar captura al paso
            if (move.fromPiece().pieceType() == Pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && enPassant[move.to().file()] == moveCounter - 1) {
                moveLog.type = MoveType.enPassant;
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveLog.enPassantSquare = move.to().previousRank(move.fromPiece().color());
                moveLog.enPassantPiece = pieceAt(moveLog.enPassantSquare);
                pieceAt(moveLog.enPassantSquare, Pieces.none);
            } else {
                moveLog.type = MoveType.normal;
            }
        }
        moveLog.pieceB = pieceAt(move.to());
        pieceAt(move.from(), Pieces.none);
        pieceAt(move.to(), move.fromPiece());
        nextPlayer();
        moveCounter++;
        return moveLog;
    }

    private void previousPlayer() {
        nextPlayer();
    }

    private void nextPlayer() {
        if (currentPlayer == blackPlayer) {
            currentPlayer = whitePlayer;
        } else {
            currentPlayer = blackPlayer;
        }
    }

    void undo(MoveLog moveLog) {
        if (moveLog.type == MoveType.normal) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.pieceB);

        } else if (moveLog.type == MoveType.enPassant) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.pieceB);
            pieceAt(moveLog.enPassantSquare, moveLog.enPassantPiece);

        } else if (moveLog.type == MoveType.castling) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.pieceB);
            pieceAt(moveLog.castlingExtraMove.from(), moveLog.castlingExtraMove.fromPiece());
            pieceAt(moveLog.castlingExtraMove.to(), Pieces.none);
        }
        whiteLeftRookMoved = moveLog.whiteLeftRookMoved;
        whiteRightRookMoved = moveLog.whiteRightRookMoved;
        whiteKingMoved = moveLog.whiteKingMoved;
        blackLeftRookMoved = moveLog.blackLeftRookMoved;
        blackRightRookMoved = moveLog.blackRightRookMoved;
        blackKingMoved = moveLog.blackKingMoved;
        enPassant[moveLog.move.to().file()] = moveLog.enPassant;
        drawCounter = moveLog.drawCounter;
        moveCounter = moveLog.moveCounter;

        previousPlayer();
    }

    boolean isCorrectMove(Square from, Square to) {
        return isCorrectMove(new Move(pieceAt(from), from, to));
    }

    boolean isCorrectMove(Move move) {
        return move.fromPiece().isCorrectMove(this, move);
    }

    public boolean moveDoesNotCreateCheck(Square from, Square to) {
        return moveDoesNotCreateCheck(new Move(pieceAt(from), from, to));
    }

    public boolean moveDoesNotCreateCheck(Move move) { //can i do this in a sandbox to avoid undo?
        //realizo la jugada, el turno pasa al contrario
        MoveLog m = play(move);

        nextPlayer(); //cambio el turno para ver si nosotros estamos en jaque
        boolean r = isInCheck();
        previousPlayer(); //lo dejo como estaba

        undo(m);

        return !r;
    }

    private boolean noMoreMovesAllowed() {
        if (isCheckmate()) {
            if (currentPlayer == blackPlayer)
                logger.info("checkmate winner is " + whitePlayer.name());
            else
                logger.info("checkmate winner is " + blackPlayer.name());
            return true;
        } else if (isADraw()) {
            logger.info("draw");
            return true;
        } else
            return false;
    }

    boolean isInCheck() {
        Square kingSquare = Square.allSquares.stream()
                .filter((square) -> pieceAt(square).pieceType() == King && isPieceOfCurrentPlayer(pieceAt(square)))
                .findAny().orElseThrow(() -> {
                    return new RuntimeException("Could not find king!");
                });

        nextPlayer();
        boolean kingCaptured = Square.allSquares.stream()
                .anyMatch((from) -> isPieceOfCurrentPlayer(pieceAt(from)) && isCorrectMove(from, kingSquare));

        previousPlayer();

        return kingCaptured;
    }

    boolean cannotMoveWithoutBeingCheck() {
        Collection<Move> moves = generateMoves();

        for (Move move : moves) {
            if (moveDoesNotCreateCheck(move.from(), move.to()))
                return false;
        }
        return true;
    }

    boolean isCheckmate() {
        if (isInCheck()) {
            return cannotMoveWithoutBeingCheck();
        } else {
            return false;
        }
    }

    boolean isADraw() {
        if (!isInCheck() && cannotMoveWithoutBeingCheck()) {
            return true;
        }

        return drawCounter == 50;
    }

    void playCastlingExtraMove(MoveLog moveLog) {
        if (moveLog.move.isCastlingQueenside()) {
            if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
                moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingQueensideWhiteFrom, castlingQueensideWhiteTo);
                pieceAt(castlingQueensideWhiteFrom, Pieces.none);
                pieceAt(castlingQueensideWhiteTo, Pieces.whiteRook);
            } else {
                moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingQueensideBlackFrom, castlingQueensideBlackTo);
                pieceAt(castlingQueensideBlackFrom, Pieces.none);
                pieceAt(castlingQueensideBlackTo, Pieces.blackRook);
            }

        } else if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
            moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingKingsideWhiteFrom, castlingKingsideWhiteTo);
            pieceAt(castlingKingsideWhiteFrom, Pieces.none);
            pieceAt(castlingKingsideWhiteTo, Pieces.whiteRook);
        } else {
            moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingKingsideBlackFrom, castlingKingsideBlackTo);
            pieceAt(castlingKingsideBlackFrom, Pieces.none);
            pieceAt(castlingKingsideBlackTo, Pieces.blackRook);
        }
    }

    public Piece pieceAt(Square square) {
        return squares[square.rank()][square.file()];
    }

    void pieceAt(Square square, Piece newPiece) {
        squares[square.rank()][square.file()] = newPiece;
    }

    Collection<Move> generateMoves() {
        return Square.allSquares.stream().map((from) -> {
            if (isPieceOfCurrentPlayer(pieceAt(from))) {
                return generateMoves(from, (move) -> moveDoesNotCreateCheck(move));
            }
            return Collections.<Move>emptyList();
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    Collection<Move> generateMoves(Square from) {
        return pieceAt(from).generateMoves(this, from);
    }

    Collection<Move> generateMoves(Square from, Predicate<Move> predicate) {
        return generateMoves(from).stream().filter(predicate).collect(Collectors.toList());
    }

    void resetWith(Board board) {
        squares = Arrays.stream(board.squares).map(Piece[]::clone).toArray(Piece[][]::new);
        enPassant = board.enPassant.clone();
        currentPlayer = board.currentPlayer;
        whiteLeftRookMoved = board.whiteLeftRookMoved;
        whiteRightRookMoved = board.whiteRightRookMoved;
        whiteKingMoved = board.whiteKingMoved;
        blackLeftRookMoved = board.blackLeftRookMoved;
        blackRightRookMoved = board.blackRightRookMoved;
        blackKingMoved = board.blackKingMoved;
        finished = board.finished;
        drawCounter = board.drawCounter;
        moveCounter = board.moveCounter;
        blackPlayer = board.blackPlayer;
        whitePlayer = board.whitePlayer;
    }

    public void reset() {
        squares = newTable();
        enPassant = new int[8];

        for (int k = 0; k < 8; k++) {
            enPassant[k] = -5; //movimiento absurdo
        }

        blackPlayer = new ComputerPlayer("computer 1", PieceColor.blackSet, 3, PlayerStrategy.F1);
        whitePlayer = new UserPlayer("user 1", PieceColor.whiteSet);
        currentPlayer = whitePlayer;

        squares[0][0] = Pieces.blackRook;
        squares[0][1] = Pieces.blackKnight;
        squares[0][2] = Pieces.blackBishop;
        squares[0][3] = Pieces.blackQueen;
        squares[0][4] = Pieces.blackKing;
        squares[0][5] = Pieces.blackBishop;
        squares[0][6] = Pieces.blackKnight;
        squares[0][7] = Pieces.blackRook;
        for (int file = 0; file < 8; file++) {
            squares[1][file] = Pieces.blackPawn;
        }

        squares[7][0] = Pieces.whiteRook;
        squares[7][1] = Pieces.whiteKnight;
        squares[7][2] = Pieces.whiteBishop;
        squares[7][3] = Pieces.whiteQueen;
        squares[7][4] = Pieces.whiteKing;
        squares[7][5] = Pieces.whiteBishop;
        squares[7][6] = Pieces.whiteKnight;
        squares[7][7] = Pieces.whiteRook;
        for (int file = 0; file < 8; file++) {
            squares[6][file] = Pieces.whitePawn;
        }
    }

    public boolean hasFinished() {
        return finished;
    }

    public void whitePlayer(Player player) {
        whitePlayer = player;
    }

    public void blackPlayer(Player player) {
        blackPlayer = player;
    }

    public Piece pieceAt(int rank, int file) {
        Square square = new Square(rank, file);
        if (!square.exists()) {
            throw new IllegalArgumentException("Square " + square + " does not exist.");
        }
        return pieceAt(square);
    }
}
