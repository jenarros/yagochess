import java.awt.*;

public class Jugada {
	Jugada() {
		ficha = -1;
		origen = new Point( 1, 0 );
		destino = new Point( 2, 0 );
	}
	Jugada( int f, Point O, Point D ) {
		ficha = f;
		origen = O;
		destino = D;
	}
    
    void MostrarJugada(){
        System.out.print("FICHA " + ficha + " DESDE [ " + (int)origen.getX() + " , " + (int)origen.getY() + " ] ");
        System.out.println("HASTA [ " + (int)destino.getX() + " , " + (int)destino.getY() + " ] ");
    }
	//c�digo de ficha a mover (1: pe�n blanco,...)
	int ficha;
	//coordenadas de origen en la matriz del tablero
	Point origen;
	//coordenadas de destino
	Point destino;
}
