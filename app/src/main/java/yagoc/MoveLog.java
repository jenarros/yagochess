package yagoc;

class MoveLog {
	final Move move;
	final boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
	final boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
	final int captura;
	final int drawCounter;
	final int moveCounter;

	MoveType type; //2=normal, 3=captura al paso, 4=enroque
	Square enPassantSquare;
	Move castlingExtraMove;
	Piece pieceB;
	Piece enPassantPiece;

	MoveLog(Board board, Move move) {
		this.move = move;
		this.whiteLeftRookMoved = board.whiteLeftRookMoved;
		this.whiteRightRookMoved = board.whiteRightRookMoved;
		this.whiteKingMoved = board.whiteKingMoved;
		this.blackLeftRookMoved = board.blackLeftRookMoved;
		this.blackRightRookMoved = board.blackRightRookMoved;
		this.blackKingMoved = board.blackKingMoved;
		this.drawCounter = board.drawCounter;
		this.moveCounter = board.moveCounter;
		this.captura = board.cap[move.to.file];
	}
}
