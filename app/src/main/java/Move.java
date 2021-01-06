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
		System.out.print("Piece " + piece + " from [ " + from.x + " , " + from.y + " ] ");
		System.out.println("to [ " + to.x + " , " + to.y + " ] ");
	}
}
