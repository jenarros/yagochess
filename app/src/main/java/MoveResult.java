import java.awt.*;

public class MoveResult {
	MoveResult() {
	}

	int tipo = -1; //2=normal, 3=captura al paso, 4=enroque
	Point squareA = new Point(0, 0);
	Point squareB = new Point(0, 0);
	Point squareC = new Point(0, 0);
	Point squareD = new Point(0, 0);
	int pieceA;
	int pieceB;
	int pieceC;
	int pieceD;
	boolean movTorreI_b, movTorreD_b, movRey_b;
	boolean movTorreI_n, movTorreD_n, movRey_n;
	boolean castlingQueenside, castlingKingside;
	int captura;
	int drawCounter;
	int moveCounter;
}
