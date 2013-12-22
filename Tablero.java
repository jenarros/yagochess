// FUNDAMENTOS DE INTELIGENCIA ARTIFICIAL
// CURSO 2002-03
// INGNENIERIA EN INFORMATICA
//Funciones desarrolladas para la práctica 1 de FIA 2002/2003
//*********************************************************************
//void moverJugador1()
//void moverJugador2()
//void moverFicha(Point origen, Point destino)
//Movimiento realizarJugada(Jugada j)
//void deshacerJugada(Movimiento m)
//boolean jugadaCorrecta(Point origen, Point destino)
//boolean jugadaProvocaJaque(Point origen, Point destino)
//boolean finPartida()
//boolean jaque()
//boolean esPosibleMover()
//boolean jaqueMate()
//boolean tablas()
//realizarEroque(Movimiento m, Point origen, Point destino)
//boolean jugadaCorrectaPeon(Point origen, Point destino)
//boolean jugadaCorrectaCaballo(Point origen, Point destino)
//boolean jugadaCorrectaAlfil(Point origen, Point destino)
//boolean jugadaCorrectaTorre(Point origen, Point destino)
//boolean jugadaCorrectaReina(Point origen, Point destino)
//boolean jugadaCorrectaRey(Point origen, Point destino)
//boolean jugadaCorrectaEnroque(Point origen, Point destino)
//void generarJugadas(LinkedList l)
//void generarJugadasPeon(int x, int y, LinkedList l)
//void generarJugadasCaballo(int x, int y, LinkedList l)
//void generarJugadasAlfil(int x, int y, LinkedList l)
//void generarJugadasTorre(int x, int y, LinkedList l)
//void generarJugadasReina(int x, int y, LinkedList l)
//void generarJugadasRey(int x, int y, LinkedList l)
//int Movimientos(int x, int y)
//int MovimientosAlfil(int x, int y)
//int MovimientosTorre(int x, int y)
//*********************************************************************
//¡¡¡¡¡¡¡¡¡¡SIN IMPLEMETAR¡¡¡¡¡¡¡¡¡¡
//TABLAS: REPETIR 3 VECES LA CONFIGURACIÓN DE LA PARTIDA
//INFORMACIÓN POR LA CONSOLA
//---- + PRUEBAS ----
import java.io.*;
import java.awt.*;
import java.util.*;
//Tablero de juego
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

public class Tablero implements Serializable {
    int[ ][ ] tab;
    int[ ] cap;//para la captura al paso
    int turno;//-1:negras, 1:blancas
    boolean movTorreI_b, movTorreD_b, movRey_b;
    boolean movTorreI_n, movTorreD_n, movRey_n;
    boolean acabada;
    int contador_tablas;
    int contador_movimientos;
    Jenm jugador1, jugador2;
    boolean enroqueL, enroqueC;
    /*las anteriores se ponen a true cuando a jugadaCorrecta devuelve
    true para alguna de ellas*/

    //turno=1-> le toca a las blancas, -1->a las negras

    //----------------------------------------------------------------------------//
    //Constructor
    public Tablero() {
        int i, j;
        tab = new int[ 8 ][ 8 ];
        cap = new int[ 8 ];

        for(int k=0; k < 8; k++) {
            cap[k] = -5;//movimiento absurdo
        }
        movTorreI_b = false;
        movTorreI_n = false;
        movTorreD_b = false;
        movTorreD_n = false;
        movRey_b = false;
        movRey_b = false;
        turno = 1; //blancas
        acabada = false;
        contador_tablas = 0;
        contador_movimientos = 0;

        enroqueL = false;
        enroqueC = false;

        //Fichas negras
        tab[ 0 ][ 0 ] = -4;
        tab[ 0 ][ 1 ] = -2;
        tab[ 0 ][ 2 ] = -3;
        tab[ 0 ][ 3 ] = -5;
        tab[ 0 ][ 4 ] = -6;
        tab[ 0 ][ 5 ] = -3;
        tab[ 0 ][ 6 ] = -2;
        tab[ 0 ][ 7 ] = -4;
        for ( j = 0;j < 8;j++ )
            tab[ 1 ][ j ] = -1;

        //Fichas blancas
        tab[ 7 ][ 0 ] = 4;
        tab[ 7 ][ 1 ] = 2;
        tab[ 7 ][ 2 ] = 3;
        tab[ 7 ][ 3 ] = 5;
        tab[ 7 ][ 4 ] = 6;
        tab[ 7 ][ 5 ] = 3;
        tab[ 7 ][ 6 ] = 2;
        tab[ 7 ][ 7 ] = 4;
        for ( j = 0;j < 8;j++ )
            tab[ 6 ][ j ] = 1;

        //Resto del tablero
        for ( i = 2;i < 6;i++ )
            for ( j = 0;j < 8;j++ )
                tab[ i ][ j ] = 0;
        //por defecto la partida es de tipo 1
        jugador1 = new Jenm("maquina1", -1, "m");
        jugador1.nivel = 3;
        jugador2 = new Jenm("usuario1", 1, "u");
    }

    //----------------------------------------------------------------------------//
    //Devuelve la casilla del tablero en función de las coordenadas físicas
    public Point devuelveCasilla ( Point p ) {
        Point p1 = new Point();
        p1.setLocation( Math.floor( ( p.getY() - 20 ) / 60 ), Math.floor( ( p.getX() - 20 ) / 60 ) );
        return p1;
    }

    //----------------------------------------------------------------------------//
    //Devuelve las coordenadas físicas correspondientes a una casilla
    public Point devuelveCoords ( int x, int y ) {
        Point p = new Point();
        p.setLocation( y * 60 + 20, x * 60 + 20 );
        return p;
    }

    //-----------------------------------------------------------------------------//
    //Mueve el jugador1
    void moverJugador1() {
        if ( jugador1.tipo.equals( "m" ) ) { //si es una maquina
            Jugada j = new Jugada();

            j = jugador1.hacerJugada( this );


            System.out.print( "Jugador 1: " );
            j.MostrarJugada();

            realizarJugada( j );
            //si el peón ha llegado al final lo cambio por una ficha
            if ( ( j.ficha == -1 && j.destino.getX() == 7 ) ||
                 ( j.ficha == 1 && j.destino.getX() == 0 ) )
                tab[ ( int ) j.destino.getX() ][ ( int ) j.destino.getY() ] = 5 * turno * ( -1 );

            Juego.p.update( Juego.p.getGraphics() );
            acabada = finPartida();
        }
    }

    //-----------------------------------------------------------------------------//
    //Mueve el jugador2
    void moverJugador2( ) {
        if ( jugador2.tipo.equals( "m" ) ) {
            Jugada j = new Jugada();

            j = jugador2.hacerJugada( this );

            System.out.print( "Jugador 2: " );
            j.MostrarJugada();

            realizarJugada( j );
            //si el peón ha llegado al final lo cambio por una ficha
            if ( ( j.ficha == -1 && j.destino.getX() == 7 ) ||
                 ( j.ficha == 1 && j.destino.getX() == 0 ) )
                tab[ ( int ) j.destino.getX() ][ ( int ) j.destino.getY() ] = 5 * turno * ( -1 );

            Juego.p.update( Juego.p.getGraphics() );
            acabada = finPartida();
        }
    }

    //------------------------------------------------------------------------------//
    //Actualiza el tablero de juego con el movimiento efectuado por el jugador
    void moverFicha( Point origen, Point destino ) {
        if ( acabada )
            return ;

        int ficha = tab[ ( int ) origen.getX() ][ ( int ) origen.getY() ];

        //si la ficha no es del turno no hago nada
        if ( turno * ficha < 0 )
            return ;

        if ( ficha < 0 && jugador1.tipo.equals( "u" )
             || ficha > 0 && jugador2.tipo.equals( "u" ) ) { //si el jugador es un usuario
            if ( !origen.equals( destino )
                 && jugadaCorrecta( origen, destino )
                 && !jugadaProvocaJaque( origen, destino ) ) {

                realizarJugada( new Jugada( ficha, origen, destino ) );
                if ( ( ficha == -1 && destino.getX() == 7 ) ||
                     ( ficha == 1 && destino.getX() == 0 ) ) {
                    try {
                        int f;
                        BufferedReader entrada =
                            new BufferedReader( new InputStreamReader( System.in ) );
                        do {
                            System.out.println( "********************** CAMBIO DE FICHA *********************" );
                            System.out.println( "Introduzca la ficha por la que desea cambiar el peón:" );
                            System.out.print( "[2:caballo | 3:alfil | 4:torre | 5:reina ] " );
                            f = Integer.valueOf( entrada.readLine() ).intValue();
                            tab[ ( int ) destino.getX() ][ ( int ) destino.getY() ] = f * turno * ( -1 );
                        } while ( f < 1 && f > 5 );
                    } catch ( Exception e ) {
                        System.out.println( "Error: " + e );
                    }
                }
                Juego.p.update( Juego.p.getGraphics() );

                acabada = finPartida();
                if ( !acabada ) {
                    if ( ficha < 0 ) {
                        System.out.print( "Jugador 1: ");
                        (new Jugada(ficha,origen,destino) ).MostrarJugada();
                        moverJugador2();
                    } else {
                        System.out.print( "Jugador 2: ");
                        (new Jugada(ficha,origen,destino) ).MostrarJugada();
                        moverJugador1();
                    }
                }
            }
        }
    }

    //realiza la jugada sobre el tablero
    Movimiento realizarJugada( Jugada j ) {
        Movimiento m = new Movimiento();

        //guardamos los datos actuales sobre enroques
        m.movTorreI_b = movTorreI_b;
        m.movTorreD_b = movTorreD_b;
        m.movRey_b = movRey_b;
        m.movTorreI_n = movTorreI_n;
        m.movTorreD_n = movTorreD_n;
        m.movRey_n = movRey_n;
        m.enroqueL = enroqueL;
        m.enroqueC = enroqueC;
        m.contador_tablas = contador_tablas;
        m.contador_movimientos = contador_movimientos;
        m.captura = cap[(int)j.destino.getY()];
        //comprobamos si están implicadas las fichas que pueden enrocar
        //y las marcamos como movidas
        if ( j.ficha == 6 )
            movRey_b = true;
        else if ( j.ficha == 4 && ( int ) j.origen.getY() == 0 )
            movTorreI_b = true;
        else if ( j.ficha == 4 && ( int ) j.origen.getY() == 7 )
            movTorreD_b = true;

        if ( j.ficha == -6 )
            movRey_n = true;
        else if ( j.ficha == -4 && ( int ) j.origen.getY() == 0 )
            movTorreI_n = true;
        else if ( j.ficha == -4 && ( int ) j.origen.getY() == 7 )
            movTorreD_n = true;

        //miramos si hay que realizar un enroque
        if (Math.abs(j.ficha) == 6
            && Math.abs(j.origen.getY() - j.destino.getY() ) == 2
            && (enroqueL || enroqueC ) ) {
            contador_tablas++;
			//System.out.println("Realizo enroque");
            realizarEnroque( m, j.origen, j.destino );
        } else { //movimiento convencional
            if ( tab[ ( int ) j.destino.getX() ][ ( int ) j.destino.getY() ] != 0 ||
                 Math.abs( j.ficha ) == 1 ) {
                //reinicio el contador por matar una ficha
                //o mover un peon
                contador_tablas = 0;
            } else
                contador_tablas++;
            //si peon avanza dos activar posible captura al paso
            if (Math.abs( j.ficha ) == 1
                && Math.abs(j.origen.getX() - j.destino.getX()) == 2) {
                cap[(int)j.destino.getY()] = contador_movimientos;
            }
            //realizar captura al paso
            //if (capturaAlPaso) {
            if(Math.abs(j.ficha) == 1
               && Math.abs(j.origen.getX() - j.destino.getX()) == 1
               && Math.abs(j.origen.getY() - j.destino.getY()) == 1
               && cap[(int)j.destino.getY()] == contador_movimientos - 1) {
                m.tipo = 3;
                m.casillaC = new Point((int)j.destino.getX() + turno, (int)j.destino.getY());
                m.fichaC = tab[ ( int ) j.destino.getX() + turno][ ( int ) j.destino.getY() ];
                tab[ ( int ) j.destino.getX() + turno][ ( int ) j.destino.getY() ] = 0;
            } else
                m.tipo = 2;
            m.casillaA = j.origen;
            m.casillaB = j.destino;
            m.fichaA = tab[ ( int ) j.origen.getX() ][ ( int ) j.origen.getY() ];
            m.fichaB = tab[ ( int ) j.destino.getX() ][ ( int ) j.destino.getY() ];
            tab[ ( int ) j.origen.getX() ][ ( int ) j.origen.getY() ] = 0;
            tab[ ( int ) j.destino.getX() ][ ( int ) j.destino.getY() ] = j.ficha;
        }
        turno *= -1;
        contador_movimientos++;
        return m;
    }

    //---------------------------------------------------------------------------//
    //Deshace la jugada caracterizada por el movimiento
    void deshacerJugada( Movimiento m ) {
        tab[ ( int ) m.casillaA.getX() ][ ( int ) m.casillaA.getY() ] = m.fichaA;
        tab[ ( int ) m.casillaB.getX() ][ ( int ) m.casillaB.getY() ] = m.fichaB;
        if ( m.tipo == 3 ) {
            tab[ ( int ) m.casillaC.getX() ][ ( int ) m.casillaC.getY() ] = m.fichaC;
        }
        if ( m.tipo == 4 ) {
			//System.out.println("Deshago enroque");
            tab[ ( int ) m.casillaC.getX() ][ ( int ) m.casillaC.getY() ] = m.fichaC;
            tab[ ( int ) m.casillaD.getX() ][ ( int ) m.casillaD.getY() ] = m.fichaD;
        }
        movTorreI_b = m.movTorreI_b;
        movTorreD_b = m.movTorreD_b;
        movRey_b = m.movRey_b;
        movTorreI_n = m.movTorreI_n;
        movTorreD_n = m.movTorreD_n;
        movRey_n = m.movRey_n;
        enroqueC = m.enroqueC;
        enroqueL = m.enroqueL;
        cap[(int)m.casillaB.getY()] = m.captura;
        contador_tablas = m.contador_tablas;
        contador_movimientos = m.contador_movimientos;

        turno *= -1;
    }

    //---------------------------------------------------------------------------//
    //Comprobación de jugada correcta para las blancas
    boolean jugadaCorrecta( Point origen, Point destino ) {
        boolean r = false;
        int ficha = tab[ ( int ) origen.getX() ][ ( int ) origen.getY() ];

        //comprobamos que no nos salimos del tablero
        if ( ( int ) destino.getX() > 7 || ( int ) destino.getX() < 0 ||
             ( int ) destino.getY() > 7 || ( int ) destino.getY() < 0 ) {
            return false;
        }

        //en el destino no debe haber una ficha nuestra
        if ( tab[ ( int ) origen.getX() ][ ( int ) origen.getY() ] * tab[ ( int ) destino.getX() ][ ( int ) destino.getY() ] > 0 )
            return false;

        //jugadas convencionales
        switch ( Math.abs( ficha ) ) {
        case 1:
            r = jugadaCorrectaPeon( origen, destino );
            break;
        case 2:
            r = jugadaCorrectaCaballo( origen, destino );
            break;
        case 3:
            r = jugadaCorrectaAlfil( origen, destino );
            break;
        case 4:
            r = jugadaCorrectaTorre( origen, destino );
            break;
        case 5:
            r = jugadaCorrectaReina( origen, destino );
            break;
        case 6:
            r = jugadaCorrectaRey( origen, destino );
            break;
        }
        return r;
    }

    //---------------------------------------------------------------------------//
    //comprueba si tras realizar la jugada el nuestro rey queda en jaque
    boolean jugadaProvocaJaque( Point origen, Point destino ) {
        Movimiento m = new Movimiento();
        int ficha = tab[ ( int ) origen.getX() ][ ( int ) origen.getY() ];

        //realizo la jugada,el turno pasa al contrario
        m = realizarJugada( new Jugada( ficha, origen, destino ) );

        turno *= -1; //cambio el turno para ver si nosotros estamos en jaque
        boolean r;
        if ( jaque() )
            r = true;
        else
            r = false;
        turno *= -1; //lo dejo como estaba

        //desago la jugada
        deshacerJugada( m );

        return r;
    }

    //-----------------------------------------------------------------------//
    //Dibuja una ficha en el tablero
    void dibujaFicha( Graphics g, int x, int y, int ficha ) {
        Point posicion = devuelveCoords ( x, y );
        if ( ficha < 0 )
            ficha = ( int ) ( 6 - ficha );
        g.drawImage( Juego.figuras[ ficha ], ( int ) posicion.getX() + 10,
                     ( int ) posicion.getY() + 10, 40, 40, Juego.p );
    }

    //-----------------------------------------------------------------------//
    //Método que se encarga de dibujar el tablero
    public void dibujar( Graphics g ) {
        int x, y;
        int ficha;

        for ( x = 20;x < 390;x += 121 )
            for ( y = 20;y < 390;y += 121 ) {
                pintaCuadrado( g, x, y, Color.white );
                pintaCuadrado( g, x + 61, y, Color.blue );
                pintaCuadrado( g, x, y + 61, Color.blue );
                pintaCuadrado( g, x + 61, y + 61, Color.white );
            }
        for ( x = 0;x < 8;x++ )
            for ( y = 0;y < 8;y++ ) {
                ficha = tab[ x ][ y ];
                if ( ficha != 0 ) {
                    dibujaFicha( g, x, y, ficha );
                }
            }
    }

    //-------------------------------------------------------------------------//
    //Método que dibuja un cuadrado del tablero de ajedrez
    private void pintaCuadrado( Graphics g, int x, int y, Color colorin ) {
        int[ ] coordX = new int[ 4 ];
        int[ ] coordY = new int[ 4 ];

        g.setColor( colorin );
        coordX[ 0 ] = x;
        coordX[ 1 ] = x;
        coordX[ 2 ] = x + 60;
        coordX[ 3 ] = x + 60;
        coordY[ 0 ] = y;
        coordY[ 1 ] = y + 60;
        coordY[ 2 ] = y + 60;
        coordY[ 3 ] = y;
        g.fillPolygon( coordX, coordY, 4 );
    }

    //-------------------------------------------------------------------------//
    //Comprueba si se ha acabado la partida
    boolean finPartida() {
        if ( jaqueMate() ) {
            if ( turno == -1 )
                System.out.println( "JAQUE MATE: GANA " + jugador2.nombre );
            else
                System.out.println( "JAQUE MATE: GANA " + jugador1.nombre );
            return true;
        } else if ( tablas() ) {
            System.out.println( "TABLAS" );
            return true;
        } else
            return false;
    }

    //-------------------------------------------------------------------------//
    //Comprueba si nos han hecho jaque el turno actual está en jaque
    boolean jaque() {
        //buscamos el rey
        int xRey = 0, yRey = 0;
        for ( int i = 0; i <= 7; i++ ) {
            for ( int j = 0; j <= 7; j++ ) {
                if ( tab[ i ][ j ] == 6 * turno ) {
                    xRey = i;
                    yRey = j;
                }
            }
        }

        //intentamos matar al rey con todas las fichas contrarias
        //cambiamos el turno por que comprobamos jugadas del contrario
        turno *= -1;
        for ( int i = 0; i <= 7; i++ ) {
            for ( int j = 0; j <= 7; j++ ) {
                if ( tab[ i ][ j ] * turno > 0 ) {
                    if ( jugadaCorrecta( new Point( i, j ), new Point( xRey, yRey ) ) ) {
                        turno *= -1;
                        //System.out.println("JAQUE AL REY "+tab[xRey][yRey]+" POR "+tab[i][j]+" DESDE "+i+","+j);
                        return true;
                    }
                }
            }
        }
        //dejamos el turno como estaba
        turno *= -1;
        return false;
    }

    //-------------------------------------------------------------------------//
    //Devuelve true si es posible mover sin quedar en jaque
    boolean esPosibleMover() {
        //si hay alguna jugada tras la cual no quedemos en jaque
        LinkedList jugadas = new LinkedList();
        Jugada j = new Jugada();

        generarJugadas( jugadas );
        //System.out.println( "Es posible mover genera " + jugadas.size() );
        while ( jugadas.size() != 0 ) {
            j = ( Jugada ) jugadas.removeFirst();
            if ( !jugadaProvocaJaque( j.origen, j.destino ) )
                return true;
        }
        return false;
    }

    //-------------------------------------------------------------------------//
    //Comprueba si nos han hecho jaque mate
    boolean jaqueMate() {
        if ( !jaque() )
            return false;

        //estamos en jaque
        if ( esPosibleMover() )
            return false;

        return true;
    }

    //-----------------------------------------------------------------------------//
    //Comprueba si la partida esta en tablas
    boolean tablas() {
        //"ahogado" el rey no está en jaque pero no puedo mover ninguna ficha
        if ( !jaque() && !esPosibleMover() ) {
            return true;
        }
        //se repite 3 veces el dibujo del tablero
        //en 50 movimientos no se movió un peón o mató una ficha
        if ( contador_tablas == 50 ) {
            return true;
        }
        return false;
    }

    //realiza el enroque
    //---------------------------------------------------------------------------//
    void realizarEnroque( Movimiento m, Point origen, Point destino ) {
		int ficha = tab[(int)origen.getX()][(int)origen.getY()];
        m.tipo = 4;

        if ( enroqueL == true && origen.getY() - destino.getY() > 0) //enroque largo
        {
            if ( ficha > 0 ) {
                m.casillaA = new Point( 7 , 0 );
                m.casillaB = new Point( 7 , 4 );
                m.casillaC = new Point( 7 , 2 );
                m.casillaD = new Point( 7 , 3 );
                m.fichaA = tab[ 7 ][ 0 ];
                m.fichaB = tab[ 7 ][ 4 ];
                m.fichaC = tab[ 7 ][ 2 ];
                m.fichaD = tab[ 7 ][ 3 ];
                tab[ 7 ][ 0 ] = 0; //torre
                tab[ 7 ][ 4 ] = 0; //rey
                tab[ 7 ][ 2 ] = 6; //rey
                tab[ 7 ][ 3 ] = 4; //torre
				//System.out.println("Realizo enroque largo en blancas");
            }
            else {
                m.casillaA = new Point( 0 , 0 );
                m.casillaB = new Point( 0 , 4 );
                m.casillaC = new Point( 0 , 2 );
                m.casillaD = new Point( 0 , 3 );
                m.fichaA = tab[ 0 ][ 0 ];
                m.fichaB = tab[ 0 ][ 4 ];
                m.fichaC = tab[ 0 ][ 2 ];
                m.fichaD = tab[ 0 ][ 3 ];
                tab[ 0 ][ 0 ] = 0; //torre
                tab[ 0 ][ 4 ] = 0; //rey
                tab[ 0 ][ 2 ] = -6; //rey
                tab[ 0 ][ 3 ] = -4; //torre
				//System.out.println("Realizo enroque largo en negras");
            }

            enroqueL = false;
        } else if ( enroqueC == true && origen.getY() - destino.getY() < 0){ //enroque corto
             if ( ficha > 0 ) {
                m.casillaA = new Point( 7 , 7 );
                m.casillaB = new Point( 7 , 4 );
                m.casillaC = new Point( 7 , 6 );
                m.casillaD = new Point( 7 , 5 );
                m.fichaA = tab[ 7 ][ 7 ];
                m.fichaB = tab[ 7 ][ 4 ];
                m.fichaC = tab[ 7 ][ 6 ];
                m.fichaD = tab[ 7 ][ 5 ];
                tab[ 7 ][ 7 ] = 0; //torre
                tab[ 7 ][ 4 ] = 0; //rey
                tab[ 7 ][ 6 ] = 6; //rey
                tab[ 7 ][ 5 ] = 4; //torre
				//System.out.println("Realizo enroque corto en blancas");
            }
            else {
                m.casillaA = new Point( 0 , 7 );
                m.casillaB = new Point( 0 , 4 );
                m.casillaC = new Point( 0 , 6 );
                m.casillaD = new Point( 0 , 5 );
                m.fichaA = tab[ 0 ][ 7 ];
                m.fichaB = tab[ 0 ][ 4 ];
                m.fichaC = tab[ 0 ][ 6 ];
                m.fichaD = tab[ 0 ][ 5 ];
                tab[ 0 ][ 7 ] = 0; //torre
                tab[ 0 ][ 4 ] = 0; //rey
                tab[ 0 ][ 6 ] = -6; //rey
                tab[ 0 ][ 5 ] = -4; //torre
				//System.out.println("Realizo enroque largo en negras");
            }
            enroqueC = false;
        }
    }

    //comprueba si la jugada es correcta para un peón
    //-------------------------------------------------------------------------//
    boolean jugadaCorrectaPeon( Point origen, Point destino ) {
        //solo se puede avanzar una casilla o dos
        if ( ( int ) destino.getX() - ( int ) origen.getX() != ( -1 ) * turno * 1 &&
             ( int ) destino.getX() - ( int ) origen.getX() != ( -1 ) * turno * 2 ) {
            //System.out.println("error: un peon no puede mover desde "
            //     + origen + " hasta " + destino);
            return false;
        }

        //si avanzamos en la misma columna el destino debe esta vacio
        if ( ( int ) destino.getY() == ( int ) origen.getY() &&
             tab[ ( int ) destino.getX() ][ ( int ) destino.getY() ] == 0 ) {
            //si avanzamos dos casillas debemos partir de la posicion
            //inicial y la casilla saltada debe estar vacía
            if ( ( ( int ) destino.getX() - ( int ) origen.getX() == ( -1 ) * turno * 2 ) &&
                 tab[( int ) destino.getX() + turno][(int) destino.getY()] == 0 &&
                 ( ( ( int ) origen.getX() == 6 && turno == 1 ) ||
                   ( ( int ) origen.getX() == 1 && turno == -1 ) ) )
                return true;

            //si avanzamos una casilla bien
            if ( ( int ) destino.getX() - ( int ) origen.getX() == ( -1 ) * turno * 1 )
                return true;
        }

        
        //si avanzamos en diagonal en el destino debe haber una ficha
        //enemiga o debe ser la jugada de "captura al paso"
        if ( ( Math.abs( ( int ) destino.getY() - ( int ) origen.getY() ) ) == 1
             && ( ( int ) destino.getX() - ( int ) origen.getX() ) == ( -1 ) * turno * 1) {
            //si en el destino hay una ficha contraria
            if ( tab[ ( int ) destino.getX() ][ ( int ) destino.getY() ] * turno < 0 ) {
                return true; //captura diagonal
            }
            //si el destino está vacio y se da la condición de captura al paso
            int c = (turno == 1) ? 3 : 4;
            if ( tab[ ( int ) destino.getX() ][ ( int ) destino.getY() ] == 0
                 && cap[(int)destino.getY()] == contador_movimientos - 1
                 && origen.getX() == c) {
                return true; //captura al paso
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un caballo
    boolean jugadaCorrectaCaballo( Point origen, Point destino ) {
        //comprobamos si el movimiento(largo) es horizontal o vertical
        if ( ( Math.abs( ( int ) origen.getX() - ( int ) destino.getX() ) == 2 ) &&
             ( Math.abs( ( int ) origen.getY() - ( int ) destino.getY() ) == 1 ) ) {
            //horizontal
            return true;
        } else
            if ( ( Math.abs( ( int ) origen.getY() - ( int ) destino.getY() ) == 2 ) &&
                 ( Math.abs( ( int ) origen.getX() - ( int ) destino.getX() ) == 1 ) ) {
                //vertical
                return true;
            } else
                return false;
    }

    //comprueba si la jugada es correcta para un alfil
    boolean jugadaCorrectaAlfil( Point origen, Point destino ) {
        if ( Math.abs( ( int ) origen.getX() - ( int ) destino.getX() ) ==
             Math.abs( ( int ) origen.getY() - ( int ) destino.getY() ) ) {
            //vamos a recorrer el movimiento de izquierda a derecha
            int ma = Math.max( ( int ) origen.getY(), ( int ) destino.getY() ); //y final
            int fila, columna, desp;

            //calculamos la casilla de inicio
            if ( ( int ) origen.getY() < ( int ) destino.getY() ) {
                fila = ( int ) origen.getX();
                columna = ( int ) origen.getY();
            } else {
                fila = ( int ) destino.getX();
                columna = ( int ) destino.getY();
            }

            //calculamos el desplazamiento
            if ( fila == Math.min( ( int ) origen.getX(), ( int ) destino.getX() ) )                 //hacia abajo
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
    boolean jugadaCorrectaTorre( Point origen, Point destino ) {
        if ( ( int ) origen.getX() == ( int ) destino.getX() ) {
            //movimiento horizontal
            int mi = Math.min( ( int ) origen.getY(), ( int ) destino.getY() ) + 1;
            int ma = Math.max( ( int ) origen.getY(), ( int ) destino.getY() );
            for ( ;mi < ma;mi++ ) {
                if ( tab[ ( int ) origen.getX() ][ mi ] != 0 )
                    return false;
            }
            return true;
        } else
            if ( ( int ) origen.getY() == ( int ) destino.getY() ) {
                //movimiento vertical
                int mi = Math.min( ( int ) origen.getX(), ( int ) destino.getX() ) + 1;
                int ma = Math.max( ( int ) origen.getX(), ( int ) destino.getX() );
                for ( ;mi < ma;mi++ ) {
                    if ( tab[ mi ][ ( int ) origen.getY() ] != 0 )
                        return false;
                }
                return true;
            } else
                return false;
    }

    //------------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un reina
    boolean jugadaCorrectaReina( Point origen, Point destino ) {
        return ( jugadaCorrectaAlfil( origen, destino ) ||
                 jugadaCorrectaTorre( origen, destino ) );

    }

    //-------------------------------------------------------------------------------//
    //comprueba si la jugada es correcta para un rey
    boolean jugadaCorrectaRey( Point origen, Point destino ) {
        if ( ( ( Math.abs( ( int ) origen.getX() - ( int ) destino.getX() ) <= 1 ) &&
               ( Math.abs( ( int ) origen.getY() - ( int ) destino.getY() ) <= 1 ) ) ||
             jugadaCorrectaEnroque( origen, destino ) ) {
            return true;
        } else
            return false;
    }

    //-------------------------------------------------------------------------------//
    //Comprueba si la jugada se corresponde con un enroque correcto: 1 si así es
    boolean jugadaCorrectaEnroque( Point origen, Point destino ) {
        int ficha = tab[ ( int ) origen.getX() ][ ( int ) origen.getY() ];

        //comprobamos si las fichas implicadas son el rey y una torre
        if ( ( ( ficha == 6 && movRey_b == false ) || ( ficha == -6 && movRey_n == false ) ) &&
             ( int ) origen.getX() == ( int ) destino.getX() &&
             ( ( int ) origen.getX() == 0 || ( int ) origen.getX() == 7 ) &&
             !jugadaProvocaJaque( origen, origen ) ) { //<-no debemos estar en jaque
				 
            if ( ( int ) destino.getY() == 2 && tab[(int)origen.getX()][0] == turno * 4) { //torre izquierda
                //blancas
                if ( ficha > 0 && movTorreI_b == false &&
                     tab[ 7 ][ 1 ] == 0 && tab[ 7 ][ 2 ] == 0 && 
					 tab[ 7 ][ 3 ] == 0 && tab[ 7 ][ 4 ] == 0 &&
                     !jugadaProvocaJaque( origen, new Point( 7, 3 ) ) &&
                     !jugadaProvocaJaque( origen, new Point( 7, 2 ) ) ) {
                    enroqueL = true;
                    return true;
                }
                //negras
                if ( ficha < 0 && movTorreI_n == false &&
                     tab[ 0 ][ 1 ] == 0 && tab[ 0 ][ 2 ] == 0 && 
					 tab[ 0 ][ 3 ] == 0 && tab[ 0 ][ 4 ] == 0 &&
                     !jugadaProvocaJaque( origen, new Point( 0, 3 ) ) &&
                     !jugadaProvocaJaque( origen, new Point( 0, 2 ) ) ) {
                    enroqueL = true;
                    return true;
                }
            } else if ( ( int ) destino.getY() == 6 && tab[(int)origen.getX()][7] == turno * 4) { //torre derecha
                //blancas
                if ( ficha > 0 && movTorreD_b == false &&
                     tab[ 7 ][ 5 ] == 0 && tab[ 7 ][ 6 ] == 0 &&
                     !jugadaProvocaJaque( origen, new Point( 7, 6 ) ) &&
                     !jugadaProvocaJaque( origen, new Point( 7, 5 ) ) ) {
                    enroqueC = true;
                    return true;
                }
                //negras
                if ( ficha < 0 && movTorreD_n == false &&
                     tab[ 0 ][ 5 ] == 0 && tab[ 0 ][ 6 ] == 0 &&
                     !jugadaProvocaJaque( origen, new Point( 0, 6 ) ) &&
                     !jugadaProvocaJaque( origen, new Point( 0, 5 ) ) ) {
                    enroqueC = true;
                    return true;
                }
            }
        }
        return false;
    }

    //Genera todas las posibles jugadas con el tablero y turno y las introduce en la lista
    public void generarJugadas( LinkedList l ) {

        for ( int i = 0; i < 8; i++ ) { //para todas las filas
            for ( int j = 0; j < 8; j++ ) { //para todas las columnas
                if ( tab[ i ][ j ] * turno > 0 ) { //en la posicion hay ficha del turno
                    switch ( Math.abs( tab[ i ][ j ] ) ) {
                    case 1:
                        generarJugadasPeon( i, j, l );
                        break;
                    case 2:
                        generarJugadasCaballo( i, j, l );
                        break;
                    case 3:
                        generarJugadasAlfil( i, j, l );
                        break;
                    case 4:
                        generarJugadasTorre( i, j, l );
                        break;
                    case 5:
                        generarJugadasReina( i, j, l );
                        break;
                    case 6:
                        generarJugadasRey( i, j, l );
                        break;
                    default:
                        System.out.println( "ERROR GRAVE" );
                    }
                }
            }
        }
    }

    //Genera todas las posibles jugadas con el tablero y la ficha [i][j] y las introduce en la lista
    public void generarJugadasPeon( int x, int y, LinkedList l ) {

        //comprobamos izquierda-centro-derecha, t indica el turno{+1,-1}
        //-p-       -P-
        //ppp   ó   ppp
        //-P-       -p-
        //izquierda
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - turno, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point( x, y ), new Point( x - turno, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - turno, y - 1 ) ) );
        }

        //centro1
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - turno, y ) ) &&
             !jugadaProvocaJaque( new Point( x, y ), new Point( x - turno, y ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - turno, y ) ) );
        }

        //centro2
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - turno - turno, y ) ) &&
             !jugadaProvocaJaque( new Point( x, y ), new Point( x - turno - turno, y ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - turno - turno, y ) ) );
        }

        //derecha
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - turno, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point( x, y ), new Point( x - turno, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - turno, y + 1 ) ) );
        }
    }

    //-----------------------------------------------------------------------------//
    //devuelve true si tras mover correctamente el caballo(x,y) no existe jaque
    public void generarJugadasCaballo( int x, int y, LinkedList l ) {
        //comprueba para las 8 casillas si podemos mover allí y si deshacemos el jaque
        //-c-c-
        //c---c
        //--C--
        //c---c
        //-c-c-
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 2, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 2, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 2, y + 1 ) ) );
        }

        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 2, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 2, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 2, y - 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 2, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 2, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 2, y + 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 2, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 2, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 2, y - 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 1, y + 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 1, y + 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 1, y + 2 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 1, y - 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 1, y - 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 1, y - 2 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 1, y + 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 1, y + 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 1, y + 2 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 1, y - 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 1, y - 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 1, y - 2 ) ) );
        }
    }

    //devuelve true si tras mover correctamente el alfil(x,y) no existe jaque
    public void generarJugadasAlfil( int x, int y, LinkedList l ) {
        //comprueba las 4 diagonales posibles, como mucho podrá avanzar 7 casillas
        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        for ( int i = 1; i <= 7;i++ ) {
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y + i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x + i, y + i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + i, y + i ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y - i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x + i, y - i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + i, y - i ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y + i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x - i, y + i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - i, y + i ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y - i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x - i, y - i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - i, y - i ) ) );
            }
        }
    }

    //devuelve true si tras mover correctamente la torre(x,y) no existe jaque
    public void generarJugadasTorre( int x, int y, LinkedList l ) {
        //comprueba las 4 rectas posibles, como mucho podrá avanzar 7 casillas
        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        for ( int i = 1; i <= 7;i++ ) {
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y ) ) &&
                 ! jugadaProvocaJaque( new Point( x, y ), new Point( x + i, y ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + i, y ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x - i, y ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - i, y ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x, y + i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x, y + i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x, y + i ) ) );
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x, y - i ) ) &&
                 !jugadaProvocaJaque( new Point( x, y ), new Point( x, y - i ) ) ) {
                l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x, y - i ) ) );
            }
        }
    }

    //devuelve true si tras mover correctamente la reina(x,y) no existe jaque
    public void generarJugadasReina( int x, int y, LinkedList l ) {
        //comprueba si se deshace para un alfil o para una torre
        //r-r-r
        //-rrr-
        //rrRrr
        //-rrr-
        //r-r-r
        generarJugadasAlfil( x, y, l );
        generarJugadasTorre( x, y, l );
    }

    //devuelve true si tras mover correctamente el rey(x,y) no existe jaque
    public void generarJugadasRey( int x, int y, LinkedList l ) {
        //comprueba para las 8 casillas si podemos mover allí y si deshacemos el jaque
        // rrr
        //rrRrr
        // rrr
        //arriba
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 1, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 1, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 1, y + 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 1, y ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 1, y ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 1, y ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x + 1, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x + 1, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x + 1, y - 1 ) ) );
        }
        //centro
        if ( jugadaCorrecta( new Point( x, y ), new Point( x, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x, y + 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x, y - 1 ) ) );
        }
        //abajo
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 1, y + 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 1, y + 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 1, y + 1 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 1, y ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 1, y ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 1, y ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x - 1, y - 1 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x - 1, y - 1 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x - 1, y - 1 ) ) );
        }
        //enroques
        if ( jugadaCorrecta( new Point( x, y ), new Point( x , y - 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x , y - 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x , y - 2 ) ) );
        }
        if ( jugadaCorrecta( new Point( x, y ), new Point( x , y + 2 ) ) &&
             !jugadaProvocaJaque( new Point ( x, y ), new Point( x , y + 2 ) ) ) {
            l.add( new Jugada( tab[ x ][ y ], new Point( x, y ), new Point( x , y + 2 ) ) );
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
        int t = turno;

        //ponemos el turno del tablero al de la ficha
        if ( tab[ x ][ y ] < 0 )
            turno = -1;
        else
            turno = 1;

        //comprueba las 4 diagonales posibles, como mucho podrá avanzar 7 casillas
        //a---a
        //-a-a-
        //--A--
        //-a-a-
        //a---a
        for ( int i = 1; i <= 7;i++ ) {
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y + i ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y - i ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y + i ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y - i ) ) ) {
                n++;
            }
        }

        turno = t;
        return n;
    }

    //Calcula los movimientos posibles de una torre
    int MovimientosTorre( int x, int y ) {
        int n = 0;
        int t = turno;

        //ponemos el turno del tablero al de la ficha
        if ( tab[ x ][ y ] < 0 )
            turno = -1;
        else
            turno = 1;

        //comprueba las 4 rectas posibles, como mucho podrá avanzar 7 casillas
        //--t--
        //--t--
        //ttTtt
        //--t--re
        //--t--
        for ( int i = 1; i <= 7;i++ ) {
            if ( jugadaCorrecta( new Point( x, y ), new Point( x + i, y ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x - i, y ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x, y + i ) ) ) {
                n++;
            }
            if ( jugadaCorrecta( new Point( x, y ), new Point( x, y - i ) ) ) {
                n++;
            }
        }

        turno = t;
        return n;
    }

}
