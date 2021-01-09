package yagoc;

class MoveResult {
	MoveResult() {
	}

	int type; //2=normal, 3=captura al paso, 4=enroque
	Square squareA;
	Square squareB;
	Square squareC;
	Square squareD;
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
