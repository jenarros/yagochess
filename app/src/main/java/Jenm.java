//CLASE PARA LA SEGUNDA PARTE DE LA PRÁCTICA 1
import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;

class Jenm implements Jugador,Serializable {
    int lado = -1;
    int nivel = 1;
    int funcion = 1;
    String nombre = "maquina1";
    String tipo = "m";//"m" (maquina) | "u" (usuario)
    Jenm() {}

    Jenm(String cad, int l, String t) {
        nombre = cad;
        lado = l;
        tipo = t;
    }

    //permite especificar de qué lado juega la máquina.
    public void fijarLado( int l ) {
        lado = l;
    }

    //permite fijar la profundidad máxima en el árbol de búsqueda
    public void fijarNivelMaximo( int n ) {
        nivel = n;
    }

    //calcula la mejor jugada para la posición actual de las piezas mediante alfa-beta
    public Jugada hacerJugada( Tablero t ) {
        JugadaValor j = new JugadaValor();

        j = AlfaBeta(nivel, t, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("/********************** Alfa-Beta *********************/");
        System.out.println("JUGADAS PROCESADAS = " + j.n + "  MINIMAX = " + j.v);
        
        while(j.camino.size() != 0){
          Jugada c = (Jugada)j.camino.removeFirst();
          System.out.println("***********Camino Recorrido****************");
          c.MostrarJugada();
        }
        
        return j.j;
    }

    //Algoritmo AlfaBeta
    //El nodo está compuesto por (nivel,Tablero)
    public JugadaValor AlfaBeta( int n, Tablero t, int alfa, int beta ) {
        LinkedList jugadas = new LinkedList();
        JugadaValor jv = new JugadaValor();
        jv.j = null;
        //si es un nodo hoja
        if ( n == 0 ) {
            if(funcion == 1)
                jv.v = F1( t );
            else if(funcion == 2)
                jv.v = F2( t );
            return jv;
        }
        //sino
        if((nivel - n ) % 2 == 0) {//si es nodo MAX
            //--jv.v = alpha
            JugadaValor a;
            Jugada j = new Jugada();
            Movimiento m = new Movimiento();
            j = null;
            jv.v = alfa;

            t.generarJugadas( jugadas );
            //MEJORA DEL ALGORITMO
            if(jugadas.size() == 0){
                jv.v = Integer.MIN_VALUE + (nivel - n + 1);
                return jv;
            }

            //Para k = 1 hasta b hacer
            while ( jugadas.size() != 0) {
                jv.n++;
                j = (Jugada)jugadas.removeFirst();
                m = t.realizarJugada(j);//----------realizo la jugada

                //alfa = max[alfa, AlfaBeta(N_k,alfa,beta)
                a = AlfaBeta(n - 1, t, jv.v, beta);
                jv.n += a.n;
                if (a.v > jv.v) {
                    jv.j = j;//mejor jugada
                    jv.v = a.v;//mejor valor
                }

                t.deshacerJugada(m);//--------------deshago la jugada

                //si alfa >= beta entonces devolver beta -PODA-
                if ( jv.v >= beta) {
                    jv.v = beta;
                    return jv;
                }
            }

            //si pierde MAX en 2 o más movimientos dejamos que juegue hasta entonces
            if(jv.j == null && j != null) {
                //camino.add(jv.j);
                jv.j = j;
            }
            //si k = b entonces devolver alfa
            return jv;
        } else {//si es nodo MIN
            JugadaValor b;
            Jugada j = new Jugada();
            Movimiento m = new Movimiento();
            j = null;
            jv.v = beta;

            t.generarJugadas(jugadas );
            //MEJORA DEL ALGORITMO
            if(jugadas.size() == 0){
                jv.v = Integer.MAX_VALUE - (nivel - n + 1);
                return jv;
            }

            //Para k = 1 hasta b hacer
            while ( jugadas.size() != 0) {
                jv.n++;
                j = (Jugada)jugadas.removeFirst();
                m = t.realizarJugada(j);//--------realizo la jugada

                //beta = min[beta, AlfaBeta(N_k,alfa,beta)]
                b = AlfaBeta(n - 1, t, alfa, jv.v);
                jv.n += b.n;
                if (b.v < jv.v) {
                    jv.j = j;//mejor jugada
                    jv.v = b.v;//mejor valor
                }

                t.deshacerJugada(m);//--------------deshago la jugada

                //si alfa >= beta entonces devolver alfa -PODA-
                if ( alfa >= jv.v) {
                    jv.v = alfa;
                    return jv;
                }
            }

            //si pierde MIN en 2 o más movimientos dejamos que juegue hasta entonces
            if(jv.j == null && j != null) {
                jv.j = j;
            }
            
            //si k = b entonces devolver beta
            return jv;
        }
    }

    //Función2 de evaluación del tablero
    public int F1( Tablero t ) {
        int f1 = 0, f2 = 0;

        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 8; j++ ) {
                if ( t.tab[ i ][ j ] * lado > 0 ) { //la ficha es mia
                    switch ( Math.abs( t.tab[ i ][ j ] ) ) {
                    case 1://cuanto más adelante mejor
                        f1 += 100 ;
                        if(t.tab[i][j] < 0)
                            f1 += i * 20;
                        else
                            f1 += (7-i) * 20;
                        //Un peón cubierto vale más
                        if(i + t.turno < 8 && i + t.turno > 0 && j - 1 > 0 && j + 1 < 8 
                            && (t.tab[i+t.turno][j - 1 ] == t.tab[i][j] 
                                || t.tab[i+t.turno][j + 1 ] == t.tab[i][j]))
                            f1 += 30;
                        break;
                    case 2://cuanto más al centro del tablero mejor
                        f1 += 300 + (3.5 - Math.abs(3.5 - j)) * 20 ;
                        if(t.tab[i][j] < 0)
                            f1 += Math.abs(3.5 - i) * 10;
                        else
                            f1 += Math.abs(3.5 - i) * 10;
                        break;
                    case 3:
                        f1 += 300 + t.Movimientos(i,j) * 10;
                        break;
                    case 4:
                        f1 += 500;
                        break;
                    case 5://cuanto más al centro mejor
                        f1 += 940 + (3.5 - Math.abs(3.5 - j)) * 20 ;
                        if(t.tab[i][j] < 0)
                            f1 += Math.abs(3.5 - i) * 10;
                        else
                            f1 += Math.abs(3.5 - i) * 10;
                        break;
                    case 6:
                        //el rey siempre está así que no lo evaluamos
                        break;
                    default:
                        System.out.println( "ERROR GRAVE" );
                    }
                }
                if ( t.tab[ i ][ j ] * lado < 0 ) { //la ficha es del contrario
                    switch ( Math.abs( t.tab[ i ][ j ] ) ) {
                    case 1://cuanto más alante mejor
                        f2 += 100 ;
                        if(t.tab[i][j] < 0)
                            f2 += i * 30;
                        else
                            f2 += (7-i) * 30;
                        if(i + t.turno < 8 && i + t.turno > 0 && j - 1 > 0 && j + 1 < 8 
                            && (t.tab[i+t.turno][j - 1 ] == t.tab[i][j] 
                                || t.tab[i+t.turno][j + 1 ] == t.tab[i][j]))
                            f2 += 20;
                        break;
                    case 2:
                        f2 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                        break;
                    case 3:
                        f2 += 330 + t.Movimientos(i,j) * 10;
                        break;
                    case 4:
                        f2 += 500;
                        break;
                    case 5:
                        f2 += 1000;
                        if(t.tab[i][j] < 0)
                            f2 += Math.abs(3.5 - i) * 10;
                        else
                            f2 += Math.abs(3.5 - i) * 10;
                        break;
                    case 6:
                        //el rey siempre está así que no lo evaluamos
                        break;
                    default:
                        System.out.println( "ERROR GRAVE" );
                    }
                }
            }
        }
        return f1 - f2;
    }
    
    //Función3 de evaluación del tablero
    public int F2( Tablero t ) {
        int f1 = 0, f2 = 0;

        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 8; j++ ) {
                if ( t.tab[ i ][ j ] * lado > 0 ) { //la ficha es mia
                    switch ( Math.abs( t.tab[ i ][ j ] ) ) {
                    case 1://cuanto más adelante mejor
                        f1 += 100 ;
                        if(t.tab[i][j] < 0)
                            f1 += i * 20;
                        else
                            f1 += (7-i) * 20;
                        break;
                    case 2://cuanto más al centro del tablero mejor
                        f1 += 300 + (3.5 - Math.abs(3.5 - j)) * 20 ;
                        if(t.tab[i][j] < 0)
                            f1 += Math.abs(3.5 - i) * 10;
                        else
                            f1 += Math.abs(3.5 - i) * 10;
                        break;
                    case 3:
                        f1 += 330 + t.Movimientos(i,j) * 10;
                        break;
                    case 4:
                        f1 += 500;
                        if(t.tab[i][j] < 0)
                            f1 += Math.abs(3.5 - i) * 15;
                        else
                            f1 += Math.abs(3.5 - i) * 15;
                        break;
                    case 5://cuanto más al centro del tablero mejor
                        f1 += 940 + (3.5 - Math.abs(3.5 - j)) * 20;
                        if(t.tab[i][j] < 0)
                            f1 += Math.abs(3.5 - i) * 10;
                        else
                            f1 += Math.abs(3.5 - i) * 10;
                        break;
                    case 6:
                        //el rey siempre está así que no lo evaluamos
                        break;
                    default:
                        System.out.println( "ERROR GRAVE" );
                    }
                }
                if ( t.tab[ i ][ j ] * lado < 0 ) { //la ficha es del contrario
                    switch ( Math.abs( t.tab[ i ][ j ] ) ) {
                    case 1://cuanto más alante mejor
                        f2 += 100 ;
                        if(t.tab[i][j] < 0)
                            f2 += i * 30;
                        else
                            f2 += (7-i) * 30;
                        break;
                    case 2:
                        f2 += 300 + (3.5 - Math.abs(3.5 - j)) * 20;
                        break;
                    case 3:
                        f2 += 330 + t.Movimientos(i,j) * 10;
                        if(t.tab[i][j] < 0)
                            f2 += Math.abs(3.5 - i) * 10;
                        else
                            f2 += Math.abs(3.5 - i) * 10;
                        break;
                    case 4:
                        f2 += 500;
                        break;
                    case 5:
                        f2 += 940 + (3.5 - Math.abs(3.5 - j)) * 10;
                        if(t.tab[i][j] < 0)
                            f2 += Math.abs(3.5 - i) * 10;
                        else
                            f2 += Math.abs(3.5 - i) * 10;
                        break;
                    case 6:
                        //el rey siempre está así que no lo evaluamos
                        break;
                    default:
                        System.out.println( "ERROR GRAVE" );
                    }
                }
            }
        }
        return f1 - f2;
    }
}
