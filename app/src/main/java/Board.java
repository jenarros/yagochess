import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    Piece[][] tab;
    int[] cap; //para la captura al paso
    SetType turn;
    boolean movTorreI_b, movTorreD_b, movRey_b;
    boolean movTorreI_n, movTorreD_n, movRey_n;
    boolean finished;
    int drawCounter;
    int moveCounter;
    private final Logger logger;
    /* Se ponen a true cuando a jugadaCorrecta devuelve true para alguna de ellas */
    boolean castlingQueenside, castlingKingside;
    Player player1, player2;

    Board(Logger logger) {
        this.logger = logger;
        tab = newTable();
        cap = new int[8];

        for (int k = 0; k < 8; k++) {
            cap[k] = -5;//movimiento absurdo
        }

        turn = SetType.whiteSet;

        tab[0][0] = Piece.blackRook;
        tab[0][1] = Piece.blackKnight;
        tab[0][2] = Piece.blackBishop;
        tab[0][3] = Piece.blackQueen;
        tab[0][4] = Piece.blackKing;
        tab[0][5] = Piece.blackBishop;
        tab[0][6] = Piece.blackKnight;
        tab[0][7] = Piece.blackRook;
        for (int file = 0; file < 8; file++) {
            tab[1][file] = Piece.blackPawn;
        }

        tab[7][0] = Piece.whiteRook;
        tab[7][1] = Piece.whiteKnight;
        tab[7][2] = Piece.whiteBishop;
        tab[7][3] = Piece.whiteQueen;
        tab[7][4] = Piece.whiteKing;
        tab[7][5] = Piece.whiteBishop;
        tab[7][6] = Piece.whiteKnight;
        tab[7][7] = Piece.whiteRook;
        for (int file = 0; file < 8; file++) {
            tab[6][file] = Piece.whitePawn;
        }

        //por defecto la partida es de tipo 1
        player1 = new ComputerPlayer("computer 1", SetType.blackSet, logger, 3, PlayerStrategies.F1);
        player2 = new UserPlayer("user 1", SetType.whiteSet);
    }

    @NotNull
    private Piece[][] newTable() {
        Piece[][] pieces = new Piece[8][8];

        for (int rank = 0; rank < pieces.length; rank++) {
            Arrays.fill(pieces[rank], Piece.none);
        }

        return pieces;
    }

    void movePlayer1() {
        if (player1.isComputer()) {
            Move move = player1.move(this);

            logger.info("Player 1: " + move.toString());

            play(move);
            ifPawnHasReachedFinalRankReplaceWithQueen(move);
        }
    }

    private void ifPawnHasReachedFinalRankReplaceWithQueen(Move move) {
        //TODO What if there is already a queen?
        if ((move.piece == Piece.blackPawn && move.to.x == 7)) {
            set(move.to, Piece.blackQueen);
        } else if (move.piece == Piece.whitePawn && move.to.x == 0) {
            set(move.to, Piece.whiteQueen);
        }

        finished = isFinished();
    }

    void movePlayer2() {
        if (player2.isComputer()) {
            Move move = player2.move(this);

            logger.info("Player 2: " + move.toString());

            play(move);
            ifPawnHasReachedFinalRankReplaceWithQueen(move);
        }
    }

    void moveIfPossible(Square from, Square to) {
        if (finished)
            return;

        Move move = new Move(get(from), from, to);

        if (turn != move.piece.set)
            return;

        if ((turn == SetType.blackSet && player1.isUser()) || (turn == SetType.whiteSet && player2.isUser())) {
            if (!from.equals(to)
                    && isCorrectMove(move)
                    && !moveCreatesCheck(move)) {

                play(move);
                if ((move.piece == Piece.blackPawn && to.x == 7) || (move.piece == Piece.whitePawn && to.x == 0)) {
                    // TODO Should be able to choose piece instead of always getting a Queen
                    if (turn == SetType.blackSet) {
                        set(to, Piece.whiteQueen);
                    } else {
                        set(to, Piece.blackQueen);
                    }
                }

                finished = isFinished();
                if (!finished) {
                    if (move.piece.set == SetType.blackSet) {
                        logger.info("Player 1: " + move.toString());
                        movePlayer2();
                    } else {
                        logger.info("Player 2: " + move.toString());
                        movePlayer1();
                    }
                }
            }
        }
    }

    MoveResult play(Move move) {
        MoveResult moveResult = new MoveResult();

        //guardamos los datos actuales sobre enroques
        moveResult.movTorreI_b = movTorreI_b;
        moveResult.movTorreD_b = movTorreD_b;
        moveResult.movRey_b = movRey_b;
        moveResult.movTorreI_n = movTorreI_n;
        moveResult.movTorreD_n = movTorreD_n;
        moveResult.movRey_n = movRey_n;
        moveResult.castlingQueenside = castlingQueenside;
        moveResult.castlingKingside = castlingKingside;
        moveResult.drawCounter = drawCounter;
        moveResult.moveCounter = moveCounter;
        moveResult.captura = cap[move.to.y];

        if (move.piece == Piece.whiteKing)
            movRey_b = true;
        else if (move.piece == Piece.whiteRook && move.from.y == 0)
            movTorreI_b = true;
        else if (move.piece == Piece.whiteRook && move.from.y == 7)
            movTorreD_b = true;

        if (move.piece == Piece.blackKing)
            movRey_n = true;
        else if (move.piece == Piece.blackRook && move.from.y == 0)
            movTorreI_n = true;
        else if (move.piece == Piece.blackRook && move.from.y == 7)
            movTorreD_n = true;

        //miramos si hay que realizar un enroque
        if (move.piece == Piece.whiteKing
                && Math.abs(move.from.y - move.to.y) == 2
                && (castlingQueenside || castlingKingside)) {
            drawCounter++;
            //logger.info("Realizo enroque");
            realizarEnroque(moveResult, move.from, move.to);
        } else {
            if (get(move.to) != Piece.none || move.piece == Piece.whitePawn) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else {
                drawCounter++;
            }
            //si peon avanza dos activar posible captura al paso
            if (move.piece.type == PieceType.pawn && Math.abs(move.from.x - move.to.x) == 2) {
                cap[move.to.y] = moveCounter;
            }
            //realizar captura al paso
            if (move.piece.type == PieceType.pawn
                    && move.rankDistance() == 1
                    && move.fileDistanceAbs() == 1
                    && cap[move.to.y] == moveCounter - 1) {
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
        movTorreI_b = moveResult.movTorreI_b;
        movTorreD_b = moveResult.movTorreD_b;
        movRey_b = moveResult.movRey_b;
        movTorreI_n = moveResult.movTorreI_n;
        movTorreD_n = moveResult.movTorreD_n;
        movRey_n = moveResult.movRey_n;
        castlingKingside = moveResult.castlingKingside;
        castlingQueenside = moveResult.castlingQueenside;
        cap[moveResult.squareB.y] = moveResult.captura;
        drawCounter = moveResult.drawCounter;
        moveCounter = moveResult.moveCounter;

        turn = turn.next();
    }

    boolean isCorrectMove(Square from, Square to) {
        return isCorrectMove(new Move(get(from), from, to));
    }

    boolean isCorrectMove(Move move) {
        boolean r = false;

        // can not capture piece of the same set
        if (!move.to.exists() || get(move.from).set == get(move.to).set)
            return false;

        switch (move.piece.type) {
            case pawn:
                r = isCorrectMoveForPawn(move);
                break;
            case knight:
                r = isCorrectMoveForKnight(move);
                break;
            case bishop:
                r = isCorrectMoveForBishop(move);
                break;
            case rook:
                r = isCorrectMoveForRook(move);
                break;
            case queen:
                r = isCorrectMoveForQueen(move);
                break;
            case king:
                r = isCorrectMoveForKing(move);
                break;
        }
        return r;
    }

    boolean moveCreatesCheck(Square from, Square to) {
        return moveCreatesCheck(new Move(get(from), from, to));
    }

    boolean moveCreatesCheck(Move move) {
        //realizo la jugada, el turno pasa al contrario
        MoveResult m = play(move);

        turn = turn.next(); //cambio el turno para ver si nosotros estamos en jaque
        boolean r = isInCheck();
        turn = turn.next(); //lo dejo como estaba

        undo(m);

        return r;
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

        // intentamos matar al rey con todas las fichas contrarias
        // cambiamos el turno por que comprobamos jugadas del contrario
        turn = turn.next();
        boolean kingCaptured = Square.allSquares.stream()
                .anyMatch((from) -> get(from).set == turn && isCorrectMove(from, kingSquare));

        // dejamos el turno como estaba
        turn = turn.next();

        return kingCaptured;
    }

    boolean canMoveWithoutBeingCheck() {
        Collection<Move> moves = generateMoves();

        for (Move move : moves) {
            if (!moveCreatesCheck(move.from, move.to))
                return true;
        }
        return false;
    }

    boolean isCheckmate() {
        if (!isInCheck())
            return false;
        else
            return !canMoveWithoutBeingCheck();
    }

    boolean isADraw() {
        if (!isInCheck() && !canMoveWithoutBeingCheck()) {
            return true;
        }
        //se repite 3 veces el dibujo del tablero
        //en 50 movimientos no se movió un peón o mató una ficha
        return drawCounter == 50;
    }

    //realiza el enroque
    void realizarEnroque(MoveResult m, Square from, Square to) {
        Piece piece = tab[from.x][from.y];
        m.type = 4;

        if (castlingQueenside && from.y - to.y > 0) {
            if (piece.set == SetType.whiteSet) {
                m.squareA = new Square(7, 0);
                m.squareB = new Square(7, 4);
                m.squareC = new Square(7, 2);
                m.squareD = new Square(7, 3);
                m.pieceA = tab[7][0];
                m.pieceB = tab[7][4];
                m.pieceC = tab[7][2];
                m.pieceD = tab[7][3];
                tab[7][0] = Piece.none;
                tab[7][4] = Piece.none;
                tab[7][2] = Piece.whiteKing;
                tab[7][3] = Piece.whiteRook;
            } else {
                m.squareA = new Square(0, 0);
                m.squareB = new Square(0, 4);
                m.squareC = new Square(0, 2);
                m.squareD = new Square(0, 3);
                m.pieceA = tab[0][0];
                m.pieceB = tab[0][4];
                m.pieceC = tab[0][2];
                m.pieceD = tab[0][3];
                tab[0][0] = Piece.none;
                tab[0][4] = Piece.none;
                tab[0][2] = Piece.blackKing;
                tab[0][3] = Piece.blackRook;
            }

            castlingQueenside = false;
        } else if (castlingKingside && from.y - to.y < 0) { //enroque corto
            if (piece.set == SetType.whiteSet) {
                m.squareA = new Square(7, 7);
                m.squareB = new Square(7, 4);
                m.squareC = new Square(7, 6);
                m.squareD = new Square(7, 5);
                m.pieceA = tab[7][7];
                m.pieceB = tab[7][4];
                m.pieceC = tab[7][6];
                m.pieceD = tab[7][5];
                tab[7][7] = Piece.none;
                tab[7][4] = Piece.none;
                tab[7][6] = Piece.whiteKing;
                tab[7][5] = Piece.whiteRook;
            } else {
                m.squareA = new Square(0, 7);
                m.squareB = new Square(0, 4);
                m.squareC = new Square(0, 6);
                m.squareD = new Square(0, 5);
                m.pieceA = tab[0][7];
                m.pieceB = tab[0][4];
                m.pieceC = tab[0][6];
                m.pieceD = tab[0][5];
                tab[0][7] = Piece.none;
                tab[0][4] = Piece.none;
                tab[0][6] = Piece.blackKing;
                tab[0][5] = Piece.blackRook;
            }
            castlingKingside = false;
        }
    }

    Piece get(Square square) {
        return tab[square.x][square.y];
    }

    void set(Square square, Piece newPiece) {
        tab[square.x][square.y] = newPiece;
    }

    boolean isCorrectMoveForPawn(Move move) {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false;
        }

        //si avanzamos en la misma columna el to debe esta vacio
        if (move.hasSameFile() && get(move.to) == Piece.none) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if (move.rankDistance() == 2 && get(move.to.previousRank(move.piece.set)) == Piece.none &&
                    ((move.from.x == 6 && turn == SetType.whiteSet) || (move.from.x == 1 && turn == SetType.blackSet)))
                return true;

            //si avanzamos una casilla bien
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
                    && cap[move.to.y] == moveCounter - 1
                    && move.from.x == c;
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
            int ma = Math.max(move.from.y, move.to.y); //y final
            int rank, file, direction;

            //calculamos la casilla de inicio
            if (move.from.y < move.to.y) {
                rank = move.from.x;
                file = move.from.y;
            } else {
                rank = move.to.x;
                file = move.to.y;
            }

            //calculamos el desplazamiento
            if (rank == Math.min(move.from.x, move.to.x))                 //hacia abajo
                direction = 1;
            else //hacia arriba
                direction = -1;

            //recorremos el movimiento
            for (file++, rank += direction; file < ma; ) {
                if (tab[rank][file] != Piece.none)
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
            int mi = Math.min(move.from.y, move.to.y) + 1;
            int ma = Math.max(move.from.y, move.to.y);
            for (; mi < ma; mi++) {
                if (tab[move.from.x][mi] != Piece.none)
                    return false;
            }
            return true;
        } else if (move.hasSameFile()) {
            //movimiento vertical
            int mi = Math.min(move.from.x, move.to.x) + 1;
            int ma = Math.max(move.from.x, move.to.x);
            for (; mi < ma; mi++) {
                if (tab[mi][move.from.y] != Piece.none)
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

        if (((move.piece == Piece.whiteKing && !movRey_b) || (move.piece == Piece.blackKing && !movRey_n)) &&
                move.hasSameRank() &&
                (move.from.x == 0 || move.from.x == 7) &&
                !moveCreatesCheck(move)) {

            if (move.to.y == 2 && tab[move.from.x][0] == move.piece.to(PieceType.rook)) {
                //blancas
                if (move.piece.set == SetType.whiteSet && !movTorreI_b &&
                        tab[7][1] == Piece.none && tab[7][2] == Piece.none &&
                        tab[7][3] == Piece.none &&
                        !moveCreatesCheck(move.from, new Square(7, 3)) &&
                        !moveCreatesCheck(move.from, new Square(7, 2))) {
                    castlingQueenside = true;
                    return true;
                }
                //negras
                if (move.piece.set == SetType.blackSet && !movTorreI_n &&
                        tab[0][1] == Piece.none && tab[0][2] == Piece.none &&
                        tab[0][3] == Piece.none && tab[0][4] == Piece.none &&
                        !moveCreatesCheck(move.from, new Square(0, 3)) &&
                        !moveCreatesCheck(move.from, new Square(0, 2))) {
                    castlingQueenside = true;
                    return true;
                }
            } else if (move.to.y == 6 && tab[move.from.x][7] == move.piece.to(PieceType.rook)) { //torre derecha
                //blancas
                if (move.piece.set == SetType.whiteSet && !movTorreD_b &&
                        tab[7][5] == Piece.none && tab[7][6] == Piece.none &&
                        !moveCreatesCheck(move.from, new Square(7, 6)) &&
                        !moveCreatesCheck(move.from, new Square(7, 5))) {
                    castlingKingside = true;
                    return true;
                }
                //negras
                if (move.piece.set == SetType.blackSet && !movTorreD_n &&
                        tab[0][5] == Piece.none && tab[0][6] == Piece.none &&
                        !moveCreatesCheck(move.from, new Square(0, 6)) &&
                        !moveCreatesCheck(move.from, new Square(0, 5))) {
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
            }
            return Collections.<Move>emptyList();
        }).flatMap((collection) -> collection.stream())
                .collect(Collectors.toList());
    }

    Collection<Move> generatePawnMoves(Square from) {
        //-p-       -P-
        //ppp   ó   ppp
        //-P-       -p-
        Piece piece = get(from);

        return Stream.of(
                from.nextRankPreviousFile(turn), // left
                from.nextRank(turn),             // ahead
                from.next2Rank(turn),            // ahead 2
                from.nextRankNextFile(turn)      // right
        ).map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move) && !moveCreatesCheck(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateKnightMoves(Square from) {
        //comprueba para las 8 casillas si podemos mover allí y si deshacemos el jaque
        //-c-c-
        //c---c
        //--C--
        //c---c
        //-c-c-
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
                .filter((move) -> isCorrectMove(move) && !moveCreatesCheck(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateBishopMoves(Square from) {
        //comprueba las 4 diagonales posibles, como mucho podrá avanzar 7 casillas
        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        Piece piece = get(from);

        return IntStream.range(1, 8).mapToObj((i) -> Stream.of(
                new Square(from.x + i, from.y + i),
                new Square(from.x + i, from.y - i),
                new Square(from.x - i, from.y + i),
                new Square(from.x - i, from.y - i)
        )).flatMap((stream) ->
                stream.map((to) -> new Move(piece, from, to))
        ).filter((move) -> isCorrectMove(move) && !moveCreatesCheck(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateRookMoves(Square from) {
        //comprueba las 4 rectas posibles, como mucho podrá avanzar 7 casillas
        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        Piece piece = get(from);
        return Stream.concat(from.otherSquaresInSameFile().stream(), from.otherSquaresInSameRank().stream())
                .map((to) -> new Move(piece, from, to))
                .filter((move) -> isCorrectMove(move) && !moveCreatesCheck(move))
                .collect(Collectors.toList());
    }

    Collection<Move> generateQueenMoves(Square from) {
        //comprueba si se deshace para un alfil o para una torre
        //r-r-r
        //-rrr-
        //rrRrr
        //-rrr-
        //r-r-r
        return Stream.concat(generateBishopMoves(from).stream(), generateRookMoves(from).stream())
                .collect(Collectors.toList());
    }

    Collection<Move> generateKingMoves(Square from) {
        // rrr
        // rRr
        // rrr
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
                .filter((move) -> isCorrectMove(move) && !moveCreatesCheck(move))
                .collect(Collectors.toList());
    }

    int possibleMoves(Square from) {
        int n = 0;

        switch (get(from).type) {
            case bishop:
                return possibleMovesForBishop(from);
            case rook:
                return possibleMovesForRook(from);
            case queen:
                return possibleMovesForBishop(from) + possibleMovesForRook(from);
            default:
                logger.warn("Possible moves isn't implemented for pawns, knights and kings");
        }
        return n;
    }

    int possibleMovesForBishop(Square from) {
        SetType t = turn;

        turn = get(from).set;

        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        Piece piece = get(from);
        int n = Long.valueOf(from.diagonalSquares().stream()
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove).count()).intValue();

        turn = t;
        return n;
    }

    int possibleMovesForRook(Square from) {
        SetType t = turn;

        turn = get(from).set;

        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        Piece piece = get(from);
        int n = Long.valueOf(from.straightSquares().stream()
                .map((to) -> new Move(piece, from, to))
                .filter(this::isCorrectMove).count()).intValue();

        turn = t;
        return n;
    }

    void resetWith(Board board) {
        tab = board.tab;
        cap = board.cap;
        turn = board.turn;
        movTorreI_b = board.movTorreI_b;
        movTorreD_b = board.movTorreD_b;
        movRey_b = board.movRey_b;
        movTorreI_n = board.movTorreI_n;
        movTorreD_n = board.movTorreD_n;
        movRey_n = board.movRey_n;
        finished = board.finished;
        drawCounter = board.drawCounter;
        moveCounter = board.moveCounter;
        player1 = board.player1;
        player2 = board.player2;
        castlingQueenside = board.castlingQueenside;
        castlingKingside = board.castlingKingside;
    }
}
