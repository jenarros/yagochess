import java.awt.*;

public class Move {
	int piece;
	Point from;
	Point to;

	Move(int f, Point O, Point D) {
		piece = f;
		from = O;
		to = D;
	}

	void print() {
		System.out.print("FICHA " + piece + " DESDE [ " + (int) from.getX() + " , " + (int) from.getY() + " ] ");
		System.out.println("HASTA [ " + (int) to.getX() + " , " + (int) to.getY() + " ] ");
	}
}
