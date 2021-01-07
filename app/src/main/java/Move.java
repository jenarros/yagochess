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

	public String toString() {
		return "Piece " + piece + " from [ " + from.x + " , " + from.y + " ] " + "to [ " + to.x + " , " + to.y + " ] ";
	}
}
