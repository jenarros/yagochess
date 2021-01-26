package yagoc;

import yagoc.pieces.Piece;
import yagoc.pieces.PieceColor;
import yagoc.pieces.Pieces;
import yagoc.players.Player;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import static yagoc.pieces.Pieces.none;

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
public class Board extends BoardState {
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

    public Board(BoardState board) {
        resetWith(board);
    }

    @Override
    public Board clone() {
        return new Board(this);
    }

    public void ifPawnHasReachedFinalRankReplaceWithQueen(Move move) {
        //TODO What if there is already a queen?
        if ((move.fromPiece().equals(Pieces.blackPawn) && move.to().rank() == 7)) {
            pieceAt(move.to(), Pieces.blackQueen);
        } else if (move.fromPiece().equals(Pieces.whitePawn) && move.to().rank() == 0) {
            pieceAt(move.to(), Pieces.whiteQueen);
        }
    }

    public boolean isPieceOfCurrentPlayer(Piece piece) {
        return piece.color() == currentPlayer.pieceColor();
    }

    public static boolean moveDoesNotCreateCheck(Board board, Move move) {
        MoveLog moveLog = board.play(move);

        boolean inCheck = isInCheck(board, move.fromPiece().color());

        board.undo(moveLog);

        return !inCheck;
    }

    public static boolean noMoreMovesAllowed(Board board) {
        if (isCheckmate(board)) {
            logger.info("checkmate winner is " + board.oppositePlayer().name());
            return true;
        } else if (isADraw(board)) {
            logger.info("draw");
            return true;
        } else
            return false;
    }

    private void previousPlayer() {
        nextPlayer();
    }

    private void nextPlayer() {
        if (currentPlayer.equals(blackPlayer)) {
            currentPlayer = whitePlayer;
        } else {
            currentPlayer = blackPlayer;
        }
    }

    public static boolean isInCheck(Board board, PieceColor color) {
        Square kingSquare = Square.allSquares.stream()
                .filter((square) -> board.pieceAt(square).pieceType() == King && board.pieceAt(square).color() == color)
                .findAny().orElseThrow(() -> new RuntimeException("Could not find " + color + " king!"));

        return Square.allSquares.stream()
                .anyMatch((from) -> !board.pieceAt(from).equals(none) && !board.pieceAt(from).color().equals(color) && board.isCorrectMove(from, kingSquare));
    }

    boolean isCorrectMove(Square from, Square to) {
        return isCorrectMove(new Move(pieceAt(from), from, to));
    }

    boolean isCorrectMove(Move move) {
        return move.fromPiece().isCorrectMove(this, move);
    }

    static boolean cannotMoveWithoutBeingCheck(Board board) {
        return board.generateMoves().stream().noneMatch((move) -> moveDoesNotCreateCheck(board, move));
    }

    static boolean isCheckmate(Board board) {
        if (isInCheck(board, board.currentPlayer.pieceColor())) {
            return cannotMoveWithoutBeingCheck(board);
        } else {
            return false;
        }
    }

    static boolean isADraw(Board board) {
        if (!isInCheck(board, board.currentPlayer.pieceColor()) && cannotMoveWithoutBeingCheck(board)) {
            return true;
        }

        return board.drawCounter == 50;
    }

    public static <T> T playAndUndo(Board board, Move move, Callable<T> callable) {
        MoveLog moveLog = board.play(move);
        try {
            T moveValue = callable.call();
            board.undo(moveLog);
            return moveValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPieceOfOppositePlayer(Piece piece) {
        return !piece.equals(none) && !piece.color().equals(currentPlayer.pieceColor());
    }

    public MoveLog play(Move move) {
        MoveLog moveLog = new MoveLog(this, move, pieceAt(move.to()));

        if (move.fromPiece().equals(Pieces.whiteKing))
            whiteKingMoved = true;
        else if (move.fromPiece().equals(Pieces.whiteRook) && move.from().file() == 0)
            whiteLeftRookMoved = true;
        else if (move.fromPiece().equals(Pieces.whiteRook) && move.from().file() == 7)
            whiteRightRookMoved = true;

        if (move.fromPiece().equals(Pieces.blackKing))
            blackKingMoved = true;
        else if (move.fromPiece().equals(Pieces.blackRook) && move.from().file() == 0)
            blackLeftRookMoved = true;
        else if (move.fromPiece().equals(Pieces.blackRook) && move.from().file() == 7)
            blackRightRookMoved = true;

        if (move.isCastling()) {
            drawCounter++;
            moveLog.type = MoveType.castling;
            playCastlingExtraMove(moveLog);
        } else {
            if (!pieceAt(move.to()).equals(none) || move.fromPiece().pieceType() == Pawn) {
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
                pieceAt(moveLog.enPassantSquare, none);
            } else {
                moveLog.type = MoveType.normal;
            }
        }

        pieceAt(move.from(), none);
        pieceAt(move.to(), move.fromPiece());
        nextPlayer();
        moveCounter++;
        return moveLog;
    }

    private void undo(MoveLog moveLog) {
        if (moveLog.type == MoveType.normal) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);

        } else if (moveLog.type == MoveType.enPassant) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);
            pieceAt(moveLog.enPassantSquare, moveLog.enPassantPiece);

        } else if (moveLog.type == MoveType.castling) {
            pieceAt(moveLog.move.from(), moveLog.move.fromPiece());
            pieceAt(moveLog.move.to(), moveLog.toPiece);
            pieceAt(moveLog.castlingExtraMove.from(), moveLog.castlingExtraMove.fromPiece());
            pieceAt(moveLog.castlingExtraMove.to(), none);
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

    public boolean moveDoesNotCreateCheck(Square from, Square to) {
        return moveDoesNotCreateCheck(this, new Move(pieceAt(from), from, to));
    }

    public Piece pieceAt(Square square) {
        return squares[square.rank()][square.file()];
    }

    void pieceAt(Square square, Piece newPiece) {
        squares[square.rank()][square.file()] = newPiece;
    }

    void playCastlingExtraMove(MoveLog moveLog) {
        if (moveLog.move.isCastlingQueenside()) {
            if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
                moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingQueensideWhiteFrom, castlingQueensideWhiteTo);
                pieceAt(castlingQueensideWhiteFrom, none);
                pieceAt(castlingQueensideWhiteTo, Pieces.whiteRook);
            } else {
                moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingQueensideBlackFrom, castlingQueensideBlackTo);
                pieceAt(castlingQueensideBlackFrom, none);
                pieceAt(castlingQueensideBlackTo, Pieces.blackRook);
            }

        } else if (moveLog.move.fromPiece().color() == PieceColor.whiteSet) {
            moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingKingsideWhiteFrom, castlingKingsideWhiteTo);
            pieceAt(castlingKingsideWhiteFrom, none);
            pieceAt(castlingKingsideWhiteTo, Pieces.whiteRook);
        } else {
            moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingKingsideBlackFrom, castlingKingsideBlackTo);
            pieceAt(castlingKingsideBlackFrom, none);
            pieceAt(castlingKingsideBlackTo, Pieces.blackRook);
        }
    }

    public Stream<Move> generateMoves(Square from) {
        return pieceAt(from).generateMoves(this, from);
    }

    Stream<Move> generateMoves(Square from, Predicate<Move> predicate) {
        return generateMoves(from).filter(predicate);
    }

    public Collection<Move> generateMoves() {
        return Square.allSquares.stream().flatMap((from) -> {
            if (isPieceOfCurrentPlayer(pieceAt(from))) {
                return generateMoves(from, (move) -> moveDoesNotCreateCheck(this, move));
            }
            return Stream.empty();
        }).collect(Collectors.toList());
    }

    public void whitePlayer(Player player) {
        if (currentPlayer.equals(whitePlayer)) {
            currentPlayer = player;
        }
        whitePlayer = player;
    }

    public void blackPlayer(Player player) {
        if (currentPlayer.equals(blackPlayer)) {
            currentPlayer = player;
        }
        blackPlayer = player;
    }

    public Piece pieceAt(int rank, int file) {
        Square square = new Square(rank, file);
        if (!square.exists()) {
            throw new IllegalArgumentException("Square " + square + " does not exist.");
        }
        return pieceAt(square);
    }

    public Player oppositePlayer() {
        if (currentPlayer.equals(whitePlayer)) {
            return blackPlayer;
        } else {
            return whitePlayer;
        }
    }
}
