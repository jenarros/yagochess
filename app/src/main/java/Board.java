import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.LinkedList;

//Codificación de las fichas blancas
// 1 Peón
// 2 Caballo
// 3 Alfil
// 4 Torre
// 5 Reina
// 6 Rey
// 4 Torre
/*
-4, -2, -3, -5, -6, -3, -2, -4,
-1, -1, -1, -1, -1, -1, -1, -1,
0,  0,  0,  0,  0,  0,  0,  0,
0,  0,  0,  0,  0,  0,  0,  0,
0,  0,  0,  0,  0,  0,  0,  0,
0,  0,  0,  0,  0,  0,  0,  0,   
1,  1,  1,  1,  1,  1,  1,  1,
4,  2,  3,  5,  6,  3,  2,  4
*/

public class Board implements Serializable {
    int[][] tab;
    int[] cap; //para la captura al paso
    int turn; //-1:dark, 1:light
    boolean movTorreI_b, movTorreD_b, movRey_b;
    boolean movTorreI_n, movTorreD_n, movRey_n;
    boolean finished;
    int drawCounter;
    int moveCounter;
    DefaultPlayer player1, player2;
    /* Se ponen a true cuando a jugadaCorrecta devuelve true para alguna de ellas */
    boolean enroqueL, enroqueC;

    public Board() {
        int i, j;
        tab = new int[8][8];
        cap = new int[8];

        for (int k = 0; k < 8; k++) {
            cap[k] = -5;//movimiento absurdo
        }
        movTorreI_b = false;
        movTorreI_n = false;
        movTorreD_b = false;
        movTorreD_n = false;
        movRey_b = false;
        movRey_b = false;
        turn = 1; //blancas
        finished = false;
        drawCounter = 0;
        moveCounter = 0;

        enroqueL = false;
        enroqueC = false;

        // dark pieces
        tab[0][0] = -4;
        tab[0][1] = -2;
        tab[0][2] = -3;
        tab[0][3] = -5;
        tab[0][4] = -6;
        tab[0][5] = -3;
        tab[0][6] = -2;
        tab[0][7] = -4;
        for (j = 0; j < 8; j++)
            tab[1][j] = -1;

        // light pieces
        tab[7][0] = 4;
        tab[7][1] = 2;
        tab[7][2] = 3;
        tab[7][3] = 5;
        tab[7][4] = 6;
        tab[7][5] = 3;
        tab[7][6] = 2;
        tab[7][7] = 4;
        for (j = 0; j < 8; j++)
            tab[6][j] = 1;

        // other squares
        for (i = 2; i < 6; i++)
            for (j = 0; j < 8; j++)
                tab[i][j] = 0;

        //por defecto la partida es de tipo 1
        player1 = new DefaultPlayer("maquina1", -1, "m");
        player1.level = 3;
        player2 = new DefaultPlayer("usuario1", 1, "u");
    }

    public Point boardSquare(Point p) {
        return new Point(Double.valueOf(Math.floor((p.y - 20) / 60)).intValue(), Double.valueOf(Math.floor((p.x - 20) / 60)).intValue());
    }

    //----------------------------------------------------------------------------//
    //Devuelve las coordenadas físicas correspondientes a una casilla
    public Point devuelveCoords(int x, int y) {
        Point p = new Point();
        p.setLocation(y * 60 + 20, x * 60 + 20);
        return p;
    }

    void movePlayer1() {
        if (player1.type.equals("m")) { //si es una maquina
            Move j = player1.move(this);


            System.out.print("Jugador 1: ");
            j.print();

            play(j);
            //si el peón ha llegado al final lo cambio por una ficha
            if ((j.piece == -1 && j.to.x == 7) ||
                    (j.piece == 1 && j.to.x == 0))
                tab[j.to.x][j.to.y] = 5 * turn * (-1);

            Game.boardPanel.update(Game.boardPanel.getGraphics());
            finished = isFinished();
        }
    }

    void movePlayer2() {
        if (player2.type.equals("m")) {
            Move j = player2.move(this);

            System.out.print("Jugador 2: ");
            j.print();

            play(j);
            //si el peón ha llegado al final lo cambio por una ficha
            if ((j.piece == -1 && j.to.x == 7) ||
                    (j.piece == 1 && j.to.x == 0))
                tab[j.to.x][j.to.y] = 5 * turn * (-1);

            Game.boardPanel.update(Game.boardPanel.getGraphics());
            finished = isFinished();
        }
    }

    void movePiece(Point from, Point to) {
        if (finished)
            return;

        int piece = tab[from.x][from.y];

        //si la piece no es del turno no hago nada
        if (turn * piece < 0)
            return;

        if (piece < 0 && player1.type.equals("u")
                || piece > 0 && player2.type.equals("u")) { //si el jugador es un usuario
            if (!from.equals(to)
                    && correctMove(from, to)
                    && !moveCreatesCheck(from, to)) {

                play(new Move(piece, from, to));
                if ((piece == -1 && to.x == 7) ||
                        (piece == 1 && to.x == 0)) {
                    try {
                        int f;
                        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
                        do {
                            System.out.println("********************** CAMBIO DE FICHA *********************");
                            System.out.println("Introduzca la piece por la que desea cambiar el peón:");
                            System.out.print("[2:caballo | 3:alfil | 4:torre | 5:reina ] ");
                            f = Integer.parseInt(entrada.readLine());
                            tab[to.x][to.y] = f * turn * (-1);
                        } while (f < 1 || f > 5);
                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                }
                Game.boardPanel.update(Game.boardPanel.getGraphics());

                finished = isFinished();
                if (!finished) {
                    if (piece < 0) {
                        System.out.print("Jugador 1: ");
                        (new Move(piece, from, to)).print();
                        movePlayer2();
                    } else {
                        System.out.print("Jugador 2: ");
                        (new Move(piece, from, to)).print();
                        movePlayer1();
                    }
                }
            }
        }
    }

    //realiza la jugada sobre el tablero
    MoveResult play(Move move) {
        MoveResult m = new MoveResult();

        //guardamos los datos actuales sobre enroques
        m.movTorreI_b = movTorreI_b;
        m.movTorreD_b = movTorreD_b;
        m.movRey_b = movRey_b;
        m.movTorreI_n = movTorreI_n;
        m.movTorreD_n = movTorreD_n;
        m.movRey_n = movRey_n;
        m.castlingQueenside = enroqueL;
        m.castlingKingside = enroqueC;
        m.drawCounter = drawCounter;
        m.moveCounter = moveCounter;
        m.captura = cap[move.to.y];
        //comprobamos si están implicadas las fichas que pueden enrocar
        //y las marcamos como movidas
        if (move.piece == 6)
            movRey_b = true;
        else if (move.piece == 4 && move.from.y == 0)
            movTorreI_b = true;
        else if (move.piece == 4 && move.from.y == 7)
            movTorreD_b = true;

        if (move.piece == -6)
            movRey_n = true;
        else if (move.piece == -4 && move.from.y == 0)
            movTorreI_n = true;
        else if (move.piece == -4 && move.from.y == 7)
            movTorreD_n = true;

        //miramos si hay que realizar un enroque
        if (Math.abs(move.piece) == 6
                && Math.abs(move.from.y - move.to.y) == 2
                && (enroqueL || enroqueC)) {
            drawCounter++;
            //System.out.println("Realizo enroque");
            realizarEnroque(m, move.from, move.to);
        } else { //movimiento convencional
            if (tab[move.to.x][move.to.y] != 0 ||
                    Math.abs(move.piece) == 1) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                drawCounter = 0;
            } else
                drawCounter++;
            //si peon avanza dos activar posible captura al paso
            if (Math.abs(move.piece) == 1
                    && Math.abs(move.from.x - move.to.x) == 2) {
                cap[move.to.y] = moveCounter;
            }
            //realizar captura al paso
            //if (capturaAlPaso) {
            if (Math.abs(move.piece) == 1
                    && Math.abs(move.from.x - move.to.x) == 1
                    && Math.abs(move.from.y - move.to.y) == 1
                    && cap[move.to.y] == moveCounter - 1) {
                m.tipo = 3;
                m.squareC = new Point(move.to.x + turn, move.to.y);
                m.pieceC = tab[move.to.x + turn][move.to.y];
                tab[move.to.x + turn][move.to.y] = 0;
            } else
                m.tipo = 2;
            m.squareA = move.from;
            m.squareB = move.to;
            m.pieceA = tab[move.from.x][move.from.y];
            m.pieceB = tab[move.to.x][move.to.y];
            tab[move.from.x][move.from.y] = 0;
            tab[move.to.x][move.to.y] = move.piece;
        }
        turn *= -1;
        moveCounter++;
        return m;
    }

    void undo(MoveResult moveResult) {
        tab[moveResult.squareA.x][moveResult.squareA.y] = moveResult.pieceA;
        tab[moveResult.squareB.x][moveResult.squareB.y] = moveResult.pieceB;
        if (moveResult.tipo == 3) {
            tab[moveResult.squareC.x][moveResult.squareC.y] = moveResult.pieceC;
        }
        if (moveResult.tipo == 4) {
            //System.out.println("Deshago enroque");
            tab[moveResult.squareC.x][moveResult.squareC.y] = moveResult.pieceC;
            tab[moveResult.squareD.x][moveResult.squareD.y] = moveResult.pieceD;
        }
        movTorreI_b = moveResult.movTorreI_b;
        movTorreD_b = moveResult.movTorreD_b;
        movRey_b = moveResult.movRey_b;
        movTorreI_n = moveResult.movTorreI_n;
        movTorreD_n = moveResult.movTorreD_n;
        movRey_n = moveResult.movRey_n;
        enroqueC = moveResult.castlingKingside;
        enroqueL = moveResult.castlingQueenside;
        cap[moveResult.squareB.y] = moveResult.captura;
        drawCounter = moveResult.drawCounter;
        moveCounter = moveResult.moveCounter;

        turn *= -1;
    }

    // Comprobación de jugada correcta para las blancas
    boolean correctMove(Point from, Point to) {
        boolean r = false;
        int piece = tab[from.x][from.y];

        //comprobamos que no nos salimos del tablero
        if (to.x > 7 || to.x < 0 || to.y > 7 || to.y < 0) {
            return false;
        }

        //en el to no debe haber una piece nuestra
        if (tab[from.x][from.y] * tab[to.x][to.y] > 0)
            return false;

        //jugadas convencionales
        switch (Math.abs(piece)) {
            case 1:
                r = jugadaCorrectaPeon(from, to);
                break;
            case 2:
                r = jugadaCorrectaCaballo(from, to);
                break;
            case 3:
                r = jugadaCorrectaAlfil(from, to);
                break;
            case 4:
                r = jugadaCorrectaTorre(from, to);
                break;
            case 5:
                r = jugadaCorrectaReina(from, to);
                break;
            case 6:
                r = jugadaCorrectaRey(from, to);
                break;
        }
        return r;
    }

    //---------------------------------------------------------------------------//
    //comprueba si tras realizar la jugada el nuestro rey queda en jaque
    boolean moveCreatesCheck(Point from, Point to) {
        int piece = tab[from.x][from.y];

        //realizo la jugada,el turno pasa al contrario
        MoveResult m = play(new Move(piece, from, to));

        turn *= -1; //cambio el turno para ver si nosotros estamos en jaque
        boolean r;
        r = isInCheck();
        turn *= -1; //lo dejo como estaba

        //desago la jugada
        undo(m);

        return r;
    }

    void drawPiece(Graphics g, int x, int y, int piece) {
        Point position = devuelveCoords(x, y);
        if (piece < 0)
            piece = 6 - piece;
        g.drawImage(Game.images[piece], position.x + 10, position.y + 10, 40, 40, Game.boardPanel);
    }

    //-----------------------------------------------------------------------//
    //Método que se encarga de dibujar el tablero
    public void draw(Graphics g) {
        int x, y;
        int ficha;

        for (x = 20; x < 390; x += 121)
            for (y = 20; y < 390; y += 121) {
                drawSquare(g, x, y, Color.white);
                drawSquare(g, x + 61, y, Color.blue);
                drawSquare(g, x, y + 61, Color.blue);
                drawSquare(g, x + 61, y + 61, Color.white);
            }
        for ( x = 0;x < 8;x++ )
            for ( y = 0;y < 8;y++ ) {
                ficha = tab[ x ][ y ];
                if ( ficha != 0 ) {
                    drawPiece(g, x, y, ficha);
                }
            }
    }

    private void drawSquare(Graphics g, int x, int y, Color colorin) {
        int[] coordX = new int[4];
        int[] coordY = new int[4];

        g.setColor(colorin);
        coordX[0] = x;
        coordX[1] = x;
        coordX[2] = x + 60;
        coordX[3] = x + 60;
        coordY[0] = y;
        coordY[1] = y + 60;
        coordY[2] = y + 60;
        coordY[3] = y;
        g.fillPolygon(coordX, coordY, 4);
    }

    boolean isFinished() {
        if (isCheckmate()) {
            if (turn == -1)
                System.out.println("JAQUE MATE: GANA " + player2.name);
            else
                System.out.println("JAQUE MATE: GANA " + player1.name);
            return true;
        } else if (isDraw()) {
            System.out.println("TABLAS");
            return true;
        } else
            return false;
    }

    boolean isInCheck() {
        //buscamos el rey
        int xRey = 0, yRey = 0;
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (tab[i][j] == 6 * turn) {
                    xRey = i;
                    yRey = j;
                }
            }
        }

        //intentamos matar al rey con todas las fichas contrarias
        //cambiamos el turno por que comprobamos jugadas del contrario
        turn *= -1;
        for ( int i = 0; i <= 7; i++ ) {
            for ( int j = 0; j <= 7; j++ ) {
                if (tab[i][j] * turn > 0) {
                    if (correctMove(new Point(i, j), new Point(xRey, yRey))) {
                        turn *= -1;
                        //System.out.println("JAQUE AL REY "+tab[xRey][yRey]+" POR "+tab[i][j]+" DESDE "+i+","+j);
                        return true;
                    }
                }
            }
        }
        //dejamos el turno como estaba
        turn *= -1;
        return false;
    }

    boolean canMoveWithoutBeingCheck() {
        LinkedList<Move> moves = new LinkedList<>();

        generateMoves(moves);
        while (moves.size() != 0) {
            Move j = moves.removeFirst();
            if (!moveCreatesCheck(j.from, j.to))
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

    boolean isDraw() {
        if (!isInCheck() && !canMoveWithoutBeingCheck()) {
            return true;
        }
        //se repite 3 veces el dibujo del tablero
        //en 50 movimientos no se movió un peón o mató una ficha
        return drawCounter == 50;
    }

    //realiza el enroque
    //---------------------------------------------------------------------------//
    void realizarEnroque(MoveResult m, Point from, Point to) {
        int ficha = tab[from.x][from.y];
        m.tipo = 4;

        if (enroqueL && from.y - to.y > 0) //enroque largo
        {
            if (ficha > 0) {
                m.squareA = new Point(7, 0);
                m.squareB = new Point(7, 4);
                m.squareC = new Point(7, 2);
                m.squareD = new Point(7, 3);
                m.pieceA = tab[7][0];
                m.pieceB = tab[7][4];
                m.pieceC = tab[7][2];
                m.pieceD = tab[7][3];
                tab[7][0] = 0; //torre
                tab[7][4] = 0; //rey
                tab[7][2] = 6; //rey
                tab[7][3] = 4; //torre
                //System.out.println("Realizo enroque largo en blancas");
            }
            else {
                m.squareA = new Point(0, 0);
                m.squareB = new Point(0, 4);
                m.squareC = new Point(0, 2);
                m.squareD = new Point(0, 3);
                m.pieceA = tab[0][0];
                m.pieceB = tab[0][4];
                m.pieceC = tab[0][2];
                m.pieceD = tab[0][3];
                tab[0][0] = 0; //torre
                tab[0][4] = 0; //rey
                tab[0][2] = -6; //rey
                tab[0][3] = -4; //torre
                //System.out.println("Realizo enroque largo en negras");
            }

            enroqueL = false;
        } else if (enroqueC && from.y - to.y < 0) { //enroque corto
            if (ficha > 0) {
                m.squareA = new Point(7, 7);
                m.squareB = new Point(7, 4);
                m.squareC = new Point(7, 6);
                m.squareD = new Point(7, 5);
                m.pieceA = tab[7][7];
                m.pieceB = tab[7][4];
                m.pieceC = tab[7][6];
                m.pieceD = tab[7][5];
                tab[7][7] = 0; //torre
                tab[7][4] = 0; //rey
                tab[7][6] = 6; //rey
                tab[7][5] = 4; //torre
                //System.out.println("Realizo enroque corto en blancas");
            } else {
                m.squareA = new Point(0, 7);
                m.squareB = new Point(0, 4);
                m.squareC = new Point(0, 6);
                m.squareD = new Point(0, 5);
                m.pieceA = tab[0][7];
                m.pieceB = tab[0][4];
                m.pieceC = tab[0][6];
                m.pieceD = tab[0][5];
                tab[0][7] = 0; //torre
                tab[0][4] = 0; //rey
                tab[0][6] = -6; //rey
                tab[0][5] = -4; //torre
                //System.out.println("Realizo enroque largo en negras");
            }
            enroqueC = false;
        }
    }

    //comprueba si la jugada es correcta para un peón
    //-------------------------------------------------------------------------//
    boolean jugadaCorrectaPeon(Point from, Point to) {
        //solo se puede avanzar una casilla o dos
        if (to.x - from.x != (-1) * turn * 1 &&
                to.x - from.x != (-1) * turn * 2) {
            //System.out.println("error: un peon no puede mover desde "
            //     + from + " hasta " + to);
            return false;
        }

        //si avanzamos en la misma columna el to debe esta vacio
        if (to.y == from.y &&
                tab[to.x][to.y] == 0) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if ((to.x - from.x == (-1) * turn * 2) &&
                    tab[to.x + turn][to.y] == 0 &&
                    ((from.x == 6 && turn == 1) ||
                            (from.x == 1 && turn == -1)))
                return true;

            //si avanzamos una casilla bien
            if (to.x - from.x == (-1) * turn * 1)
                return true;
        }


        //si avanzamos en diagonal en el to debe haber una ficha
        //enemiga o debe ser la jugada de "captura al paso"
        if ((Math.abs(to.y - from.y)) == 1
                && (to.x - from.x) == (-1) * turn * 1) {
            //si en el to hay una ficha contraria
            if (tab[to.x][to.y] * turn < 0) {
                return true; //captura diagonal
            }
            //si el to está vacio y se da la condición de captura al paso
            int c = (turn == 1) ? 3 : 4;
            return tab[to.x][to.y] == 0
                    && cap[to.y] == moveCounter - 1
                    && from.x == c; //captura al paso
        }
        return false;
    }

    //-----------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un caballo
    boolean jugadaCorrectaCaballo(Point from, Point to) {
        //comprobamos si el movimiento(largo) es horizontal o vertical
        //vertical
        if ((Math.abs(from.x - to.x) == 2) &&
                (Math.abs(from.y - to.y) == 1)) {
            //horizontal
            return true;
        } else
            return (Math.abs(from.y - to.y) == 2) &&
                    (Math.abs(from.x - to.x) == 1);
    }

    //comprueba si la jugada es correcta para un alfil
    boolean jugadaCorrectaAlfil(Point from, Point to) {
        if (Math.abs(from.x - to.x) ==
                Math.abs(from.y - to.y)) {
            //vamos a recorrer el movimiento de izquierda a derecha
            int ma = Math.max(from.y, to.y); //y final
            int fila, columna, desp;

            //calculamos la casilla de inicio
            if (from.y < to.y) {
                fila = from.x;
                columna = from.y;
            } else {
                fila = to.x;
                columna = to.y;
            }

            //calculamos el desplazamiento
            if (fila == Math.min(from.x, to.x))                 //hacia abajo
                desp = 1;
            else //hacia arriba
                desp = -1;

            //recorremos el movimiento
            for ( columna++, fila += desp;columna < ma; ) {
                if ( tab[ fila ][ columna ] != 0 )
                    return false;
                fila += desp;
                columna++;
            }
            return true;
        } else
            return false;
    }

    //comprueba si la jugada es correcta para un torre
    boolean jugadaCorrectaTorre(Point from, Point to) {
        if (from.x == to.x) {
            //movimiento horizontal
            int mi = Math.min(from.y, to.y) + 1;
            int ma = Math.max(from.y, to.y);
            for (; mi < ma; mi++) {
                if (tab[from.x][mi] != 0)
                    return false;
            }
            return true;
        } else if (from.y == to.y) {
            //movimiento vertical
            int mi = Math.min(from.x, to.x) + 1;
            int ma = Math.max(from.x, to.x);
            for (; mi < ma; mi++) {
                if (tab[mi][from.y] != 0)
                    return false;
            }
            return true;
        } else
            return false;
    }

    //------------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un reina
    boolean jugadaCorrectaReina(Point from, Point to) {
        return (jugadaCorrectaAlfil(from, to) ||
                jugadaCorrectaTorre(from, to));

    }

    //-------------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un rey
    boolean jugadaCorrectaRey(Point from, Point to) {
        return ((Math.abs(from.x - to.x) <= 1) &&
                (Math.abs(from.y - to.y) <= 1)) ||
                jugadaCorrectaEnroque(from, to);
    }

    //-------------------------------------------------------------------------------//
    //Comprueba si la jugada se corresponde con un enroque correcto: 1 si así es
    boolean jugadaCorrectaEnroque(Point from, Point to) {
        int ficha = tab[from.x][from.y];

        //comprobamos si las fichas implicadas son el rey y una torre
        if (((ficha == 6 && !movRey_b) || (ficha == -6 && !movRey_n)) &&
                from.x == to.x &&
                (from.x == 0 || from.x == 7) &&
                !moveCreatesCheck(from, from)) { //<-no debemos estar en jaque

            if (to.y == 2 && tab[from.x][0] == turn * 4) { //torre izquierda
                //blancas
                if (ficha > 0 && !movTorreI_b &&
                        tab[7][1] == 0 && tab[7][2] == 0 &&
                        tab[7][3] == 0 && tab[7][4] == 0 &&
                        !moveCreatesCheck(from, new Point(7, 3)) &&
                        !moveCreatesCheck(from, new Point(7, 2))) {
                    enroqueL = true;
                    return true;
                }
                //negras
                if (ficha < 0 && !movTorreI_n &&
                        tab[0][1] == 0 && tab[0][2] == 0 &&
                        tab[0][3] == 0 && tab[0][4] == 0 &&
                        !moveCreatesCheck(from, new Point(0, 3)) &&
                        !moveCreatesCheck(from, new Point(0, 2))) {
                    enroqueL = true;
                    return true;
                }
            } else if (to.y == 6 && tab[from.x][7] == turn * 4) { //torre derecha
                //blancas
                if (ficha > 0 && !movTorreD_b &&
                        tab[7][5] == 0 && tab[7][6] == 0 &&
                        !moveCreatesCheck(from, new Point(7, 6)) &&
                        !moveCreatesCheck(from, new Point(7, 5))) {
                    enroqueC = true;
                    return true;
                }
                //negras
                if (ficha < 0 && !movTorreD_n &&
                        tab[0][5] == 0 && tab[0][6] == 0 &&
                        !moveCreatesCheck(from, new Point(0, 6)) &&
                        !moveCreatesCheck(from, new Point(0, 5))) {
                    enroqueC = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void generateMoves(LinkedList<Move> l) {

        for (int i = 0; i < 8; i++) { //para todas las filas
            for (int j = 0; j < 8; j++) { //para todas las columnas
                if (tab[i][j] * turn > 0) { //en la posicion hay ficha del turno
                    switch (Math.abs(tab[i][j])) {
                        case 1:
                            generarJugadasPeon(i, j, l);
                            break;
                        case 2:
                            generarJugadasCaballo(i, j, l);
                            break;
                        case 3:
                            generarJugadasAlfil(i, j, l);
                            break;
                        case 4:
                            generarJugadasTorre(i, j, l);
                            break;
                        case 5:
                            generarJugadasReina(i, j, l);
                            break;
                        case 6:
                            generarJugadasRey(i, j, l);
                            break;
                        default:
                            System.out.println("ERROR GRAVE");
                    }
                }
            }
        }
    }

    //Genera todas las posibles jugadas con el tablero y la ficha [i][j] y las introduce en la lista
    public void generarJugadasPeon(int x, int y, LinkedList<Move> l) {

        //comprobamos izquierda-centro-derecha, t indica el turno{+1,-1}
        //-p-       -P-
        //ppp   ó   ppp
        //-P-       -p-
        //izquierda
        if (correctMove(new Point(x, y), new Point(x - turn, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - turn, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - turn, y - 1)));
        }

        //centro1
        if (correctMove(new Point(x, y), new Point(x - turn, y)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - turn, y))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - turn, y)));
        }

        //centro2
        if (correctMove(new Point(x, y), new Point(x - turn - turn, y)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - turn - turn, y))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - turn - turn, y)));
        }

        //derecha
        if (correctMove(new Point(x, y), new Point(x - turn, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - turn, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - turn, y + 1)));
        }
    }

    //-----------------------------------------------------------------------------//
    //devuelve true si tras mover correctamente el caballo(x,y) no existe jaque
    public void generarJugadasCaballo(int x, int y, LinkedList<Move> l) {
        //comprueba para las 8 casillas si podemos mover allí y si deshacemos el jaque
        //-c-c-
        //c---c
        //--C--
        //c---c
        //-c-c-
        if (correctMove(new Point(x, y), new Point(x + 2, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 2, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 2, y + 1)));
        }

        if (correctMove(new Point(x, y), new Point(x + 2, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 2, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 2, y - 1)));
        }
        if (correctMove(new Point(x, y), new Point(x - 2, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 2, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 2, y + 1)));
        }
        if (correctMove(new Point(x, y), new Point(x - 2, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 2, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 2, y - 1)));
        }
        if (correctMove(new Point(x, y), new Point(x + 1, y + 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 1, y + 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 1, y + 2)));
        }
        if (correctMove(new Point(x, y), new Point(x + 1, y - 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 1, y - 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 1, y - 2)));
        }
        if (correctMove(new Point(x, y), new Point(x - 1, y + 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 1, y + 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 1, y + 2)));
        }
        if (correctMove(new Point(x, y), new Point(x - 1, y - 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 1, y - 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 1, y - 2)));
        }
    }

    //devuelve true si tras mover correctamente el alfil(x,y) no existe jaque
    public void generarJugadasAlfil(int x, int y, LinkedList<Move> l) {
        //comprueba las 4 diagonales posibles, como mucho podrá avanzar 7 casillas
        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        for (int i = 1; i <= 7; i++) {
            if (correctMove(new Point(x, y), new Point(x + i, y + i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x + i, y + i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x + i, y + i)));
            }
            if (correctMove(new Point(x, y), new Point(x + i, y - i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x + i, y - i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x + i, y - i)));
            }
            if (correctMove(new Point(x, y), new Point(x - i, y + i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x - i, y + i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x - i, y + i)));
            }
            if (correctMove(new Point(x, y), new Point(x - i, y - i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x - i, y - i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x - i, y - i)));
            }
        }
    }

    //devuelve true si tras mover correctamente la torre(x,y) no existe jaque
    public void generarJugadasTorre(int x, int y, LinkedList<Move> l) {
        //comprueba las 4 rectas posibles, como mucho podrá avanzar 7 casillas
        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        for (int i = 1; i <= 7; i++) {
            if (correctMove(new Point(x, y), new Point(x + i, y)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x + i, y))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x + i, y)));
            }
            if (correctMove(new Point(x, y), new Point(x - i, y)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x - i, y))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x - i, y)));
            }
            if (correctMove(new Point(x, y), new Point(x, y + i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x, y + i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y + i)));
            }
            if (correctMove(new Point(x, y), new Point(x, y - i)) &&
                    !moveCreatesCheck(new Point(x, y), new Point(x, y - i))) {
                l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y - i)));
            }
        }
    }

    //devuelve true si tras mover correctamente la reina(x,y) no existe jaque
    public void generarJugadasReina(int x, int y, LinkedList<Move> l) {
        //comprueba si se deshace para un alfil o para una torre
        //r-r-r
        //-rrr-
        //rrRrr
        //-rrr-
        //r-r-r
        generarJugadasAlfil(x, y, l);
        generarJugadasTorre(x, y, l);
    }

    //devuelve true si tras mover correctamente el rey(x,y) no existe jaque
    public void generarJugadasRey(int x, int y, LinkedList<Move> l) {
        //comprueba para las 8 casillas si podemos mover allí y si deshacemos el jaque
        // rrr
        //rrRrr
        // rrr
        //arriba
        if (correctMove(new Point(x, y), new Point(x + 1, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 1, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 1, y + 1)));
        }
        if (correctMove(new Point(x, y), new Point(x + 1, y)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 1, y))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 1, y)));
        }
        if (correctMove(new Point(x, y), new Point(x + 1, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x + 1, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x + 1, y - 1)));
        }
        //centro
        if (correctMove(new Point(x, y), new Point(x, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y + 1)));
        }
        if (correctMove(new Point(x, y), new Point(x, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y - 1)));
        }
        //abajo
        if (correctMove(new Point(x, y), new Point(x - 1, y + 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 1, y + 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 1, y + 1)));
        }
        if (correctMove(new Point(x, y), new Point(x - 1, y)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 1, y))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 1, y)));
        }
        if (correctMove(new Point(x, y), new Point(x - 1, y - 1)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x - 1, y - 1))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x - 1, y - 1)));
        }
        //enroques
        if (correctMove(new Point(x, y), new Point(x, y - 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x, y - 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y - 2)));
        }
        if (correctMove(new Point(x, y), new Point(x, y + 2)) &&
                !moveCreatesCheck(new Point(x, y), new Point(x, y + 2))) {
            l.add(new Move(tab[x][y], new Point(x, y), new Point(x, y + 2)));
        }
    }

    //Calcula los movimientos posibles de una ficha
    int Movimientos( int x, int y ) {
        int n = 0;

        switch ( Math.abs( tab[ x ][ y ] ) ) {
        case 3:
            return MovimientosAlfil( x, y );
        case 4:
            return MovimientosTorre( x, y );
        case 5:
            return MovimientosAlfil( x, y ) + MovimientosTorre( x, y );
        default:
            System.out.println( "Cuidado: Movimientos no está implementada para peones, caballos y reyes" );
        }
        return n;
    }

    //Calcula los movimientos posibles de un alfil
    int MovimientosAlfil( int x, int y ) {
        int n = 0;
        int t = turn;

        //ponemos el turno del tablero al de la ficha
        if ( tab[ x ][ y ] < 0 )
            turn = -1;
        else
            turn = 1;

        //comprueba las 4 diagonales posibles, como mucho podrá avanzar 7 casillas
        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        for ( int i = 1; i <= 7;i++ ) {
            if (correctMove(new Point(x, y), new Point(x + i, y + i))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x + i, y - i))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x - i, y + i))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x - i, y - i))) {
                n++;
            }
        }

        turn = t;
        return n;
    }

    //Calcula los movimientos posibles de una torre
    int MovimientosTorre( int x, int y ) {
        int n = 0;
        int t = turn;

        //ponemos el turno del tablero al de la ficha
        if ( tab[ x ][ y ] < 0 )
            turn = -1;
        else
            turn = 1;

        //comprueba las 4 rectas posibles, como mucho podrá avanzar 7 casillas
        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        for ( int i = 1; i <= 7;i++ ) {
            if (correctMove(new Point(x, y), new Point(x + i, y))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x - i, y))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x, y + i))) {
                n++;
            }
            if (correctMove(new Point(x, y), new Point(x, y - i))) {
                n++;
            }
        }

        turn = t;
        return n;
    }
}
