package yagoc;

class MoveResult {
	MoveResult() {
	}

	int type = -1; //2=normal, 3=captura al paso, 4=enroque
	Square squareA = new Square(0, 0);
	Square squareB = new Square(0, 0);
	Square squareC = new Square(0, 0);
	Square squareD = new Square(0, 0);
	Piece pieceA;
	Piece pieceB;
	Piece pieceC;
	Piece pieceD;
	boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
	boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
	boolean castlingQueenside, castlingKingside;
	int captura;
	int drawCounter;
	int moveCounter;
}
