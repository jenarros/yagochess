package yagoc;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static yagoc.Square.*;

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
    private transient final Logger logger;

    private Piece[][] squares;
    int[] cap; //para la captura al paso
    SetType turn;
    boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
    boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
    boolean finished;
    int drawCounter;
    int moveCounter;
    /* Se ponen a true cuando a jugadaCorrecta devuelve true para alguna de ellas */
    boolean castlingQueenside, castlingKingside;
    Player player1, player2;

    Board(Logger logger) {
        this.logger = logger;
        reset();
    }

    @NotNull
    private Piece[][] newTable() {
        Piece[][] pieces = new Piece[8][8];

        for (Piece[] piece : pieces) {
            Arrays.fill(piece, Piece.none);
        }

        return pieces;
    }

    void movePlayer1() {
        if (player1.isComputer()) {
            Move move = player1.move(this);

            logger.info(player1.name() + " " + move.toString());

            play(move);
            ifPawnHasReachedFinalRankReplaceWithQueen(move);
        }
    }

    private void ifPawnHasReachedFinalRankReplaceWithQueen(Move move) {
        //TODO What if there is already a queen?
        if ((move.piece == Piece.blackPawn && move.to.rank == 7)) {
            set(move.to, Piece.blackQueen);
        } else if (move.piece == Piece.whitePawn && move.to.rank == 0) {
            set(move.to, Piece.whiteQueen);
        }

        finished = isFinished();
    }

    void movePlayer2() {
        if (player2.isComputer()) {
            Move move = player2.move(this);

            logger.info(player2.name() + " " + move.toString());

            play(move);
            ifPawnHasReachedFinalRankReplaceWithQueen(move);
        }
    }

    boolean moveIfPossible(Square from, Square to) {
        Move move = new Move(get(from), from, to);
        if (finished || turn != move.piece.set) {
            return false;
        } else if ((turn == SetType.blackSet && player1.isUser()) || (turn == SetType.whiteSet && player2.isUser())) {
            if (!from.equals(to)
                    && isCorrectMove(move)
                    && moveDoesNotCreateCheck(move)) {

                play(move);
                if ((move.piece == Piece.blackPawn && to.rank == 7) || (move.piece == Piece.whitePawn && to.rank == 0)) {
                    // TODO Should be able to choose piece instead of always getting a Queen
                    set(to, move.piece.to(PieceType.queen));
                }

                finished = isFinished();
                if (!finished) {
                    if (move.piece.set == SetType.blackSet) {
                        logger.info(player1.name() + " " + move.toString());
                        movePlayer2();
                    } else {
                        logger.info(player2.name() + " " + move.toString());
                        movePlayer1();
                    }
                }
                return true;
            }
        }

        return false;
    }

    MoveResult play(Move move) {
        MoveResult moveResult = new MoveResult();

        //guardamos los datos actuales sobre enroques
        moveResult.whiteLeftRookMoved = whiteLeftRookMoved;
        moveResult.whiteRightRookMoved = whiteRightRookMoved;
        moveResult.whiteKingMoved = whiteKingMoved;
        moveResult.blackLeftRookMoved = blackLeftRookMoved;
        moveResult.blackRightRookMoved = blackRightRookMoved;
        moveResult.blackKingMoved = blackKingMoved;
        moveResult.castlingQueenside = castlingQueenside;
        moveResult.castlingKingside = castlingKingside;
        moveResult.drawCounter = drawCounter;
        moveResult.moveCounter = moveCounter;
        moveResult.captura = cap[move.to.file];

        if (move.piece == Piece.whiteKing)
            whiteKingMoved = true;
        else if (move.piece == Piece.whiteRook && move.from.file == 0)
            whiteLeftRookMoved = true;
        else if (move.piece == Piece.whiteRook && move.from.file == 7)
            whiteRightRookMoved = true;

        if (move.piece == Piece.blackKing)
            blackKingMoved = true;
        else if (move.piece == Piece.blackRook && move.from.file == 0)
            blackLeftRookMoved = true;
        else if (move.piece == Piece.blackRook && move.from.file == 7)
            blackRightRookMoved = true;

        //miramos si hay que realizar un enroque
        if (move.piece == Piece.whiteKing
                && move.fileDistanceAbs() == 2
                && (castlingQueenside || castlingKingside)) {
            drawCounter++;
            playCastling(moveResult, move);
        } else {
            if (get(move.to) != Piece.none || move.piece == Piece.whitePawn) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else {
                drawCounter++;
            }
            //si peon avanza dos activar posible captura al paso
            if (move.piece.type == PieceType.pawn && move.rankDistanceAbs() == 2) {
                cap[move.to.file] = moveCounter;
            }
            //realizar captura al paso
            if (move.piece.type == PieceType.pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && cap[move.to.file] == moveCounter - 1) {
                moveResult.type = 3;
                // i.e. for whites
                // turn=1, to.x = 2, to.y = 5, squareC = (3,5)
                moveResult.squareC = move.to.previousRank(move.piece.set);
                moveResult.pieceC = get(moveResult.squareC);
                set(moveResult.squareC, Piece.none);
            } else {
                moveResult.type = 2;
            }
            moveResult.squareA = move.from;
            moveResult.squareB = move.to;
            moveResult.pieceA = get(move.from);
            moveResult.pieceB = get(move.to);
            set(move.from, Piece.none);
            set(move.to, move.piece);
        }
        turn = turn.next();
        moveCounter++;
        return moveResult;
    }

    void undo(MoveResult moveResult) {
        set(moveResult.squareA, moveResult.pieceA);
        set(moveResult.squareB, moveResult.pieceB);
        if (moveResult.type == 3) {
            set(moveResult.squareC, moveResult.pieceC);
        }
        if (moveResult.type == 4) {
            //logger.info("Deshago enroque");
            set(moveResult.squareC, moveResult.pieceC);
            set(moveResult.squareD, moveResult.pieceD);
        }
        whiteLeftRookMoved = moveResult.whiteLeftRookMoved;
        whiteRightRookMoved = moveResult.whiteRightRookMoved;
        whiteKingMoved = moveResult.whiteKingMoved;
        blackLeftRookMoved = moveResult.blackLeftRookMoved;
        blackRightRookMoved = moveResult.blackRightRookMoved;
        blackKingMoved = moveResult.blackKingMoved;
        castlingKingside = moveResult.castlingKingside;
        castlingQueenside = moveResult.castlingQueenside;
        cap[moveResult.squareB.file] = moveResult.captura;
        drawCounter = moveResult.drawCounter;
        moveCounter = moveResult.moveCounter;

        turn = turn.previous();
    }

    boolean isCorrectMove(Square from, Square to) {
        return isCorrectMove(new Move(get(from), from, to));
    }

    boolean isCorrectMove(Move move) {
        // can not capture piece of the same set
        if (!move.to.exists() || get(move.from).set == get(move.to).set)
            return false;

        switch (move.piece.type) {
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
        return moveDoesNotCreateCheck(new Move(get(from), from, to));
    }

    boolean moveDoesNotCreateCheck(Move move) { //can i do this in a sandbox to avoid undo?
        //realizo la jugada, el turno pasa al contrario
        MoveResult m = play(move);

        turn = turn.next(); //cambio el turno para ver si nosotros estamos en jaque
        boolean r = isInCheck();
        turn = turn.next(); //lo dejo como estaba

        undo(m);

        return !r;
    }

    boolean isFinished() {
        if (isCheckmate()) {
            if (turn == SetType.blackSet)
                logger.info("checkmate winner is " + player2.name());
            else
                logger.info("checkmate winner is " + player1.name());
            return true;
        } else if (isADraw()) {
            logger.info("draw");
            return true;
        } else
            return false;
    }

    boolean isInCheck() {
        Square kingSquare = Square.allSquares.stream()
                .filter((square) -> get(square).type == PieceType.king && get(square).set == turn)
                .findAny().orElseThrow();

        turn = turn.next();
        boolean kingCaptured = Square.allSquares.stream()
                .anyMatch((from) -> get(from).set == turn && isCorrectMove(from, kingSquare));

        turn = turn.previous();

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

    void playCastling(MoveResult moveResult, Move move) {
        moveResult.type = 4;

        if (castlingQueenside && move.movesLeft()) {
            if (move.piece.set == SetType.whiteSet) {
                moveResult.squareA = castlingQueensideWhiteA;
                moveResult.squareB = castlingQueensideWhiteB;
                moveResult.squareC = castlingQueensideWhiteC;
                moveResult.squareD = castlingQueensideWhiteD;
                moveResult.pieceA = get(castlingQueensideWhiteA);
                moveResult.pieceB = get(castlingQueensideWhiteB);
                moveResult.pieceC = get(castlingQueensideWhiteC);
                moveResult.pieceD = get(castlingQueensideWhiteD);
                set(castlingQueensideWhiteA, Piece.none);
                set(castlingQueensideWhiteB, Piece.none);
                set(castlingQueensideWhiteC, Piece.whiteKing);
                set(castlingQueensideWhiteD, Piece.whiteRook);
            } else {
                moveResult.squareA = castlingQueensideBlackA;
                moveResult.squareB = castlingQueensideBlackB;
                moveResult.squareC = castlingQueensideBlackC;
                moveResult.squareD = castlingQueensideBlackD;
                moveResult.pieceA = get(castlingQueensideBlackA);
                moveResult.pieceB = get(castlingQueensideBlackB);
                moveResult.pieceC = get(castlingQueensideBlackC);
                moveResult.pieceD = get(castlingQueensideBlackD);
                set(castlingQueensideBlackA, Piece.none);
                set(castlingQueensideBlackB, Piece.none);
                set(castlingQueensideBlackC, Piece.blackKing);
                set(castlingQueensideBlackD, Piece.blackRook);
            }

            castlingQueenside = false;
        } else if (castlingKingside && move.movesRight()) {
            if (move.piece.set == SetType.whiteSet) {
                moveResult.squareA = castlingKingsideWhiteA;
                moveResult.squareB = castlingKingsideWhiteB;
                moveResult.squareC = castlingKingsideWhiteC;
                moveResult.squareD = castlingKingsideWhiteD;
                moveResult.pieceA = get(castlingKingsideWhiteA);
                moveResult.pieceB = get(castlingKingsideWhiteB);
                moveResult.pieceC = get(castlingKingsideWhiteC);
                moveResult.pieceD = get(castlingKingsideWhiteD);
                set(castlingKingsideWhiteA, Piece.none);
                set(castlingKingsideWhiteB, Piece.none);
                set(castlingKingsideWhiteC, Piece.whiteKing);
                set(castlingKingsideWhiteD, Piece.whiteRook);
            } else {
                moveResult.squareA = castlingKingsideBlackA;
                moveResult.squareB = castlingKingsideBlackB;
                moveResult.squareC = castlingKingsideBlackC;
                moveResult.squareD = castlingKingsideBlackD;
                moveResult.pieceA = get(castlingKingsideBlackA);
                moveResult.pieceB = get(castlingKingsideBlackB);
                moveResult.pieceC = get(castlingKingsideBlackC);
                moveResult.pieceD = get(castlingKingsideBlackD);
                set(castlingKingsideBlackA, Piece.none);
                set(castlingKingsideBlackB, Piece.none);
                set(castlingKingsideBlackC, Piece.blackKing);
                set(castlingKingsideBlackD, Piece.blackRook);
            }
            castlingKingside = false;
        }
    }

    Piece get(Square square) {
        return squares[square.rank][square.file];
    }

    void set(Square square, Piece newPiece) {
        squares[square.rank][square.file] = newPiece;
    }

    boolean isCorrectMoveForPawn(Move move) {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false;
        }

        // straight ahead
        if (move.hasSameFile() && get(move.to) == Piece.none) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if (move.rankDistance() == 2 && get(move.to.previousRank(move.piece.set)) == Piece.none &&
                    ((move.from.rank == 6 && turn == SetType.whiteSet) || (move.from.rank == 1 && turn == SetType.blackSet)))
                return true;

            if (move.rankDistance() == 1)
                return true;
        }

        if (move.fileDistanceAbs() == 1 && move.rankDistance() == 1) {
            // capture
            if (get(move.to).set == turn.next()) {
                return true;
            }

            // en passant
            int c = (turn == SetType.whiteSet) ? 3 : 4;
            return move.piece == Piece.none
                    && cap[move.to.file] == moveCounter - 1
                    && move.from.rank == c;
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
                if (squares[rank][file] != Piece.none)
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
                if (squares[move.from.rank][mi] != Piece.none)
                    return false;
            }
            return true;
        } else if (move.hasSameFile()) {
            //movimiento vertical
            int mi = Math.min(move.from.rank, move.to.rank) + 1;
            int ma = Math.max(move.from.rank, move.to.rank);
            for (; mi < ma; mi++) {
                if (squares[mi][move.from.file] != Piece.none)
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
        if (((move.piece == Piece.whiteKing && !whiteKingMoved) || (move.piece == Piece.blackKing && !blackKingMoved)) &&
                move.hasSameRank() &&
                (move.from.rank == 0 || move.from.rank == 7) &&
                moveDoesNotCreateCheck(move)) {

            if (move.to.file == 2 && squares[move.from.rank][0] == move.piece.to(PieceType.rook)) {
                //blancas
                if (move.piece.set == SetType.whiteSet && !whiteLeftRookMoved &&
                        squares[7][1] == Piece.none && squares[7][2] == Piece.none &&
                        squares[7][3] == Piece.none &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 3)) &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 2))) {
                    castlingQueenside = true;
                    return true;
                }
                //negras
                if (move.piece.set == SetType.blackSet && !blackLeftRookMoved &&
                        squares[0][1] == Piece.none && squares[0][2] == Piece.none &&
                        squares[0][3] == Piece.none && squares[0][4] == Piece.none &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 3)) &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 2))) {
                    castlingQueenside = true;
                    return true;
                }
            } else if (move.to.file == 6 && squares[move.from.rank][7] == move.piece.to(PieceType.rook)) { //torre derecha
                //blancas
                if (move.piece.set == SetType.whiteSet && !whiteRightRookMoved &&
                        squares[7][5] == Piece.none && squares[7][6] == Piece.none &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 6)) &&
                        moveDoesNotCreateCheck(move.from, new Square(7, 5))) {
                    castlingKingside = true;
                    return true;
                }
                //negras
                if (move.piece.set == SetType.blackSet && !blackRightRookMoved &&
                        squares[0][5] == Piece.none && squares[0][6] == Piece.none &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 6)) &&
                        moveDoesNotCreateCheck(move.from, new Square(0, 5))) {
                    castlingKingside = true;
                    return true;
                }
            }
        }
        return false;
    }

    Collection<Move> generateMoves() {
        return Square.allSquares.stream().map((from) -> {
            if (get(from).set == turn) {
                return generateMoves(from, (move) -> moveDoesNotCreateCheck(move));
            }
            return Collections.<Move>emptyList();
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

    Collection<Move> generateMoves(Square from) {
        switch (get(from).type) {
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
        Piece piece = get(from);

        return Stream.of(
                from.nextRankPreviousFile(turn), // left
                from.nextRank(turn),             // ahead
                from.next2Rank(turn),            // ahead 2
                from.nextRankNextFile(turn)      // right
        ).map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateKnightMoves(Square from) {
        Piece piece = get(from);
        return Stream.of(
                from.next2Rank(piece.set).nextFile(piece.set),
                from.next2Rank(piece.set).previousFile(piece.set),
                from.previous2Rank(piece.set).nextFile(piece.set),
                from.previous2Rank(piece.set).previousFile(piece.set),
                from.next2File(piece.set).nextRank(piece.set),
                from.next2File(piece.set).previousRank(piece.set),
                from.previous2File(piece.set).nextRank(piece.set),
                from.previous2File(piece.set).previousRank(piece.set)
        ).map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateBishopMoves(Square from) {
        Piece piece = get(from);

        return from.diagonalSquares()
                .stream().map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateRookMoves(Square from) {
        Piece piece = get(from);
        return from.straightSquares().stream()
                .map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateQueenMoves(Square from) {
        return Stream.concat(generateBishopMoves(from).stream(), generateRookMoves(from).stream())
                .collect(Collectors.toList());
    }

    Collection<Move> generateKingMoves(Square from) {
        Piece piece = get(from);
        return Stream.of(
                from.nextRank(piece.set),
                from.nextRank(piece.set).previousFile(piece.set),
                from.nextRank(piece.set).nextFile(piece.set),
                from.previousRank(piece.set),
                from.previousRank(piece.set).nextFile(piece.set),
                from.previousRank(piece.set).previousFile(piece.set),
                from.nextFile(piece.set),
                from.previousFile(piece.set)
        ).map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move))
                .collect(Collectors.toList());
    }

    void resetWith(Board board) {
        squares = board.squares;
        cap = board.cap;
        turn = board.turn;
        whiteLeftRookMoved = board.whiteLeftRookMoved;
        whiteRightRookMoved = board.whiteRightRookMoved;
        whiteKingMoved = board.whiteKingMoved;
        blackLeftRookMoved = board.blackLeftRookMoved;
        blackRightRookMoved = board.blackRightRookMoved;
        blackKingMoved = board.blackKingMoved;
        finished = board.finished;
        drawCounter = board.drawCounter;
        moveCounter = board.moveCounter;
        player1 = board.player1;
        player2 = board.player2;
        castlingQueenside = board.castlingQueenside;
        castlingKingside = board.castlingKingside;
    }

    public void reset() {
        squares = newTable();
        cap = new int[8];

        for (int k = 0; k < 8; k++) {
            cap[k] = -5; //movimiento absurdo
        }

        turn = SetType.whiteSet;

        squares[0][0] = Piece.blackRook;
        squares[0][1] = Piece.blackKnight;
        squares[0][2] = Piece.blackBishop;
        squares[0][3] = Piece.blackQueen;
        squares[0][4] = Piece.blackKing;
        squares[0][5] = Piece.blackBishop;
        squares[0][6] = Piece.blackKnight;
        squares[0][7] = Piece.blackRook;
        for (int file = 0; file < 8; file++) {
            squares[1][file] = Piece.blackPawn;
        }

        squares[7][0] = Piece.whiteRook;
        squares[7][1] = Piece.whiteKnight;
        squares[7][2] = Piece.whiteBishop;
        squares[7][3] = Piece.whiteQueen;
        squares[7][4] = Piece.whiteKing;
        squares[7][5] = Piece.whiteBishop;
        squares[7][6] = Piece.whiteKnight;
        squares[7][7] = Piece.whiteRook;
        for (int file = 0; file < 8; file++) {
            squares[6][file] = Piece.whitePawn;
        }

        player1 = new ComputerPlayer("computer 1", SetType.blackSet, logger, 3, PlayerStrategies.F1);
        player2 = new UserPlayer("user 1", SetType.whiteSet);
    }
}
