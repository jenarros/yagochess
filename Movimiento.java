import java.awt.*;

public class Movimiento {
	Movimiento(){
	}
	int tipo = -1;//2=normal, 3=captura al paso, 4=enroque
	Point casillaA = new Point (0,0);
	Point casillaB = new Point (0,0);
	Point casillaC = new Point (0,0);
	Point casillaD = new Point (0,0);
	int fichaA;
	int	fichaB;
	int	fichaC;
	int	fichaD;
	boolean movTorreI_b, movTorreD_b, movRey_b;
	boolean movTorreI_n, movTorreD_n, movRey_n;
	boolean enroqueL, enroqueC;
    int captura;
	int contador_tablas;
    int contador_movimientos;
}
