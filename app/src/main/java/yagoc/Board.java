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
import static yagoc.pieces.PieceType.king;
import static yagoc.pieces.PieceType.pawn;

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
class Board implements Serializable {
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

    Board() {
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
        if ((move.fromPiece == Pieces.blackPawn && move.to.rank == 7)) {
            pieceAt(move.to, Pieces.blackQueen);
        } else if (move.fromPiece == Pieces.whitePawn && move.to.rank == 0) {
            pieceAt(move.to, Pieces.whiteQueen);
        }

        finished = isFinished();
    }

    boolean isPieceOfCurrentPlayer(Piece piece) {
        return piece.setType() == currentPlayer.setType();
    }

    boolean isPieceOfOppositePlayer(Piece piece) {
        return piece != Pieces.none && piece.setType() != currentPlayer.setType();
    }

    boolean moveIfPossible(Square from, Square to) {
        Move move = new Move(pieceAt(from), from, to);
        if (finished || !isPieceOfCurrentPlayer(move.fromPiece)) {
            return false;
        } else if ((currentPlayer == blackPlayer && blackPlayer.isUser()) || (currentPlayer == whitePlayer && whitePlayer.isUser())) {
            if (!from.equals(to)
                    && isCorrectMove(move)
                    && moveDoesNotCreateCheck(move)) {

                play(move);
                if ((move.fromPiece == Pieces.blackPawn && to.rank == 7) || (move.fromPiece == Pieces.whitePawn && to.rank == 0)) {
                    // TODO Should be able to choose piece instead of always getting a Queen
                    pieceAt(to, move.fromPiece.switchTo(PieceType.queen));
                }

                finished = isFinished();

                logger.info(move.toString());

                return true;
            }
        }

        return false;
    }

    MoveLog play(Move move) {
        MoveLog moveLog = new MoveLog(this, move);

        if (move.fromPiece == Pieces.whiteKing)
            whiteKingMoved = true;
        else if (move.fromPiece == Pieces.whiteRook && move.from.file == 0)
            whiteLeftRookMoved = true;
        else if (move.fromPiece == Pieces.whiteRook && move.from.file == 7)
            whiteRightRookMoved = true;

        if (move.fromPiece == Pieces.blackKing)
            blackKingMoved = true;
        else if (move.fromPiece == Pieces.blackRook && move.from.file == 0)
            blackLeftRookMoved = true;
        else if (move.fromPiece == Pieces.blackRook && move.from.file == 7)
            blackRightRookMoved = true;

        if (move.isCastling()) {
            drawCounter++;
            moveLog.type = MoveType.castling;
            playCastlingExtraMove(moveLog);
        } else {
            if (pieceAt(move.to) != Pieces.none || move.fromPiece.pieceType() == pawn) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else {
                drawCounter++;
            }
            //si peon avanza dos activar posible captura al paso
            if (move.fromPiece.pieceType() == pawn && move.rankDistanceAbs() == 2) {
                enPassant[move.to.file] = moveCounter;
            }
            //realizar captura al paso
            if (move.fromPiece.pieceType() == pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && enPassant[move.to.file] == moveCounter - 1) {
                moveLog.type = MoveType.enPassant;
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveLog.enPassantSquare = move.to.previousRank(move.fromPiece.setType());
                moveLog.enPassantPiece = pieceAt(moveLog.enPassantSquare);
                pieceAt(moveLog.enPassantSquare, Pieces.none);
            } else {
                moveLog.type = MoveType.normal;
            }
        }
        moveLog.pieceB = pieceAt(move.to);
        pieceAt(move.from, Pieces.none);
        pieceAt(move.to, move.fromPiece);
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
            pieceAt(moveLog.move.from, moveLog.move.fromPiece);
            pieceAt(moveLog.move.to, moveLog.pieceB);

        } else if (moveLog.type == MoveType.enPassant) {
            pieceAt(moveLog.move.from, moveLog.move.fromPiece);
            pieceAt(moveLog.move.to, moveLog.pieceB);
            pieceAt(moveLog.enPassantSquare, moveLog.enPassantPiece);

        } else if (moveLog.type == MoveType.castling) {
            pieceAt(moveLog.move.from, moveLog.move.fromPiece);
            pieceAt(moveLog.move.to, moveLog.pieceB);
            pieceAt(moveLog.castlingExtraMove.from, moveLog.castlingExtraMove.fromPiece);
            pieceAt(moveLog.castlingExtraMove.to, Pieces.none);
        }
        whiteLeftRookMoved = moveLog.whiteLeftRookMoved;
        whiteRightRookMoved = moveLog.whiteRightRookMoved;
        whiteKingMoved = moveLog.whiteKingMoved;
        blackLeftRookMoved = moveLog.blackLeftRookMoved;
        blackRightRookMoved = moveLog.blackRightRookMoved;
        blackKingMoved = moveLog.blackKingMoved;
        enPassant[moveLog.move.to.file] = moveLog.enPassant;
        drawCounter = moveLog.drawCounter;
        moveCounter = moveLog.moveCounter;

        previousPlayer();
    }

    boolean isCorrectMove(Square from, Square to) {
        return isCorrectMove(new Move(pieceAt(from), from, to));
    }

    boolean isCorrectMove(Move move) {
        // can not capture piece of the same set
        if (pieceAt(move.from).setType() == pieceAt(move.to).setType()) {
            return false;
        }

        switch (move.fromPiece.pieceType()) {
            case pawn:
                return isCorrectMoveForPawn(move);
            case knight:
                return isCorrectMoveForKnight(move);
            case bishop:
                return isCorrectMoveForBishop(move);
            case rook:
                return isCorrectMoveForRook(move);
            case queen:
                return isCorrectMoveForQueen(move);
            case king:
                return isCorrectMoveForKing(move);
        }
        return false;
    }

    boolean moveDoesNotCreateCheck(Square from, Square to) {
        return moveDoesNotCreateCheck(new Move(pieceAt(from), from, to));
    }

    boolean moveDoesNotCreateCheck(Move move) { //can i do this in a sandbox to avoid undo?
        //realizo la jugada, el turno pasa al contrario
        MoveLog m = play(move);

        nextPlayer(); //cambio el turno para ver si nosotros estamos en jaque
        boolean r = isInCheck();
        previousPlayer(); //lo dejo como estaba

        undo(m);

        return !r;
    }

    boolean isFinished() {
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
                .filter((square) -> pieceAt(square).pieceType() == king && isPieceOfCurrentPlayer(pieceAt(square)))
                .findAny().orElseThrow();

        nextPlayer();
        boolean kingCaptured = Square.allSquares.stream()
                .anyMatch((from) -> isPieceOfCurrentPlayer(pieceAt(from)) && isCorrectMove(from, kingSquare));

        previousPlayer();

        return kingCaptured;
    }

    boolean canMoveWithoutBeingCheck() {
        Collection<Move> moves = generateMoves();

        for (Move move : moves) {
            if (moveDoesNotCreateCheck(move.from, move.to))
                return true;
        }
        return false;
    }

    boolean isCheckmate() {
        if (isInCheck()) {
            return !canMoveWithoutBeingCheck();
        } else {
            return false;
        }
    }

    boolean isADraw() {
        if (!isInCheck() && !canMoveWithoutBeingCheck()) {
            return true;
        }

        return drawCounter == 50;
    }

    void playCastlingExtraMove(MoveLog moveLog) {
        if (moveLog.move.isCastlingQueenside()) {
            if (moveLog.move.fromPiece.setType() == SetType.whiteSet) {
                moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingQueensideWhiteFrom, castlingQueensideWhiteTo);
                pieceAt(castlingQueensideWhiteFrom, Pieces.none);
                pieceAt(castlingQueensideWhiteTo, Pieces.whiteRook);
            } else {
                moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingQueensideBlackFrom, castlingQueensideBlackTo);
                pieceAt(castlingQueensideBlackFrom, Pieces.none);
                pieceAt(castlingQueensideBlackTo, Pieces.blackRook);
            }

        } else if (moveLog.move.fromPiece.setType() == SetType.whiteSet) {
            moveLog.castlingExtraMove = new Move(Pieces.whiteRook, castlingKingsideWhiteFrom, castlingKingsideWhiteTo);
            pieceAt(castlingKingsideWhiteFrom, Pieces.none);
            pieceAt(castlingKingsideWhiteTo, Pieces.whiteRook);
        } else {
            moveLog.castlingExtraMove = new Move(Pieces.blackRook, castlingKingsideBlackFrom, castlingKingsideBlackTo);
            pieceAt(castlingKingsideBlackFrom, Pieces.none);
            pieceAt(castlingKingsideBlackTo, Pieces.blackRook);
        }
    }

    Piece pieceAt(Square square) {
        return squares[square.rank][square.file];
    }

    void pieceAt(Square square, Piece newPiece) {
        squares[square.rank][square.file] = newPiece;
    }

    boolean isCorrectMoveForPawn(Move move) {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false;
        }

        // straight ahead
        if (move.hasSameFile() && pieceAt(move.to) == Pieces.none) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if (move.rankDistance() == 2 && pieceAt(move.to.previousRank(move.fromPiece.setType())) == Pieces.none &&
                    ((move.from.rank == 6 && currentPlayer == whitePlayer) || (move.from.rank == 1 && currentPlayer == blackPlayer)))
                return true;

            if (move.rankDistance() == 1)
                return true;
        }

        if (move.fileDistanceAbs() == 1 && move.rankDistance() == 1) {
            // capture
            if (isPieceOfOppositePlayer(pieceAt(move.to))) {
                return true;
            }

            // en passant
            return pieceAt(move.to) == Pieces.none
                    && enPassant[move.to.file] == moveCounter - 1
                    && move.from.rank == ((currentPlayer == whitePlayer) ? 3 : 4);
        }
        return false;
    }

    boolean isCorrectMoveForKnight(Move move) {
        if (move.rankDistanceAbs() == 2 && move.fileDistanceAbs() == 1) {
            return true;
        } else {
            return move.fileDistanceAbs() == 2 && move.rankDistanceAbs() == 1;
        }
    }

    boolean isCorrectMoveForBishop(Move move) {
        if (move.rankDistanceAbs() == move.fileDistanceAbs()) {
            //vamos a recorrer el movimiento de izquierda a derecha
            int ma = Math.max(move.from.file, move.to.file); //y final
            int rank, file, direction;

            //calculamos la casilla de inicio
            if (move.from.file < move.to.file) {
                rank = move.from.rank;
                file = move.from.file;
            } else {
                rank = move.to.rank;
                file = move.to.file;
            }

            //calculamos el desplazamiento
            if (rank == Math.min(move.from.rank, move.to.rank))                 //hacia abajo
                direction = 1;
            else //hacia arriba
                direction = -1;

            //recorremos el movimiento
            for (file++, rank += direction; file < ma; ) {
                if (squares[rank][file] != Pieces.none)
                    return false;
                rank += direction;
                file++;
            }
            return true;
        } else {
            return false;
        }
    }

    boolean isCorrectMoveForRook(Move move) {
        if (move.hasSameRank()) {
            //movimiento horizontal
            int mi = Math.min(move.from.file, move.to.file) + 1;
            int ma = Math.max(move.from.file, move.to.file);
            for (; mi < ma; mi++) {
                if (squares[move.from.rank][mi] != Pieces.none)
                    return false;
            }
            return true;
        } else if (move.hasSameFile()) {
            //movimiento vertical
            int mi = Math.min(move.from.rank, move.to.rank) + 1;
            int ma = Math.max(move.from.rank, move.to.rank);
            for (; mi < ma; mi++) {
                if (squares[mi][move.from.file] != Pieces.none)
                    return false;
            }
            return true;
        } else
            return false;
    }

    boolean isCorrectMoveForQueen(Move move) {
        return (isCorrectMoveForBishop(move) || isCorrectMoveForRook(move));
    }

    boolean isCorrectMoveForKing(Move move) {
        return (move.fileDistanceAbs() <= 1 && move.rankDistanceAbs() <= 1) || isCorrectCastling(move);
    }

    boolean isCorrectCastling(Move move) {
        if (((move.from.rank == 7 && move.fromPiece == Pieces.whiteKing && !whiteKingMoved) ||
                (move.from.rank == 0 && move.fromPiece == Pieces.blackKing && !blackKingMoved)) &&
                move.hasSameRank() &&
                moveDoesNotCreateCheck(move)) {

            if (move.to.file == 2 && squares[move.from.rank][0] == move.fromPiece.switchTo(PieceType.rook)) {
                //blancas
                if (move.fromPiece.setType() == SetType.whiteSet && !whiteLeftRookMoved &&
                        squares[7][1] == Pieces.none && squares[7][2] == Pieces.none &&
                        squares[7][3] == Pieces.none &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 3)) &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 2))) {
                    return true;
                }
                //negras
                return move.fromPiece.setType() == SetType.blackSet && !blackLeftRookMoved &&
                        squares[0][1] == Pieces.none && squares[0][2] == Pieces.none &&
                        squares[0][3] == Pieces.none && squares[0][4] == Pieces.none &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 3)) &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 2));
            } else if (move.to.file == 6 && squares[move.from.rank][7] == move.fromPiece.switchTo(PieceType.rook)) { //torre derecha
                //blancas
                if (move.fromPiece.setType() == SetType.whiteSet && !whiteRightRookMoved &&
                        squares[7][5] == Pieces.none && squares[7][6] == Pieces.none &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 6)) &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 5))) {
                    return true;
                }
                //negras
                return move.fromPiece.setType() == SetType.blackSet && !blackRightRookMoved &&
                        squares[0][5] == Pieces.none && squares[0][6] == Pieces.none &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 6)) &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 5));
            }
        }
        return false;
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
        switch (pieceAt(from).pieceType()) {
            case pawn:
                return generatePawnMoves(from);
            case knight:
                return generateKnightMoves(from);
            case bishop:
                return generateBishopMoves(from);
            case rook:
                return generateRookMoves(from);
            case queen:
                return generateQueenMoves(from);
            case king:
                return generateKingMoves(from);
        }
        return Collections.emptyList();
    }

    Collection<Move> generateMoves(Square from, Predicate<Move> predicate) {
        return generateMoves(from).stream().filter(predicate).collect(Collectors.toList());
    }

    Collection<Move> generatePawnMoves(Square from) {
        Piece piece = pieceAt(from);

        return Stream.of(
                from.nextRankPreviousFile(currentPlayer.setType()), // left
                from.nextRank(currentPlayer.setType()),             // ahead
                from.next2Rank(currentPlayer.setType()),            // ahead 2
                from.nextRankNextFile(currentPlayer.setType())      // right
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove)
                .collect(Collectors.toList());
    }

    Collection<Move> generateKnightMoves(Square from) {
        Piece piece = pieceAt(from);
        return Stream.of(
                from.next2Rank(piece.setType()).nextFile(piece.setType()),
                from.next2Rank(piece.setType()).previousFile(piece.setType()),
                from.previous2Rank(piece.setType()).nextFile(piece.setType()),
                from.previous2Rank(piece.setType()).previousFile(piece.setType()),
                from.next2File(piece.setType()).nextRank(piece.setType()),
                from.next2File(piece.setType()).previousRank(piece.setType()),
                from.previous2File(piece.setType()).nextRank(piece.setType()),
                from.previous2File(piece.setType()).previousRank(piece.setType())
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove)
                .collect(Collectors.toList());
    }

    Collection<Move> generateBishopMoves(Square from) {
        Piece piece = pieceAt(from);

        return from.diagonalSquares()
                .stream().map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove)
                .collect(Collectors.toList());
    }

    Collection<Move> generateRookMoves(Square from) {
        Piece piece = pieceAt(from);
        return from.straightSquares().stream()
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove)
                .collect(Collectors.toList());
    }

    Collection<Move> generateQueenMoves(Square from) {
        return Stream.concat(generateBishopMoves(from).stream(), generateRookMoves(from).stream())
                .collect(Collectors.toList());
    }

    Collection<Move> generateKingMoves(Square from) {
        Piece piece = pieceAt(from);
        return Stream.of(
                from.nextRank(piece.setType()),
                from.nextRank(piece.setType()).previousFile(piece.setType()),
                from.nextRank(piece.setType()).nextFile(piece.setType()),
                from.previousRank(piece.setType()),
                from.previousRank(piece.setType()).nextFile(piece.setType()),
                from.previousRank(piece.setType()).previousFile(piece.setType()),
                from.nextFile(piece.setType()),
                from.previousFile(piece.setType())
        ).filter(Square::exists)
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove)
                .collect(Collectors.toList());
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

        blackPlayer = new ComputerPlayer("computer 1", SetType.blackSet, 3, PlayerStrategy.F1);
        whitePlayer = new UserPlayer("user 1", SetType.whiteSet);
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
}
