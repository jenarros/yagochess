package yagoc;

class MoveLog {
	final Move move;
	final boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
	final boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
	final int enPassant;
	final int drawCounter;
	final int moveCounter;

	MoveType type; //2=normal, 3=captura al paso, 4=enroque
	Square enPassantSquare;
	Move castlingExtraMove;
	Piece pieceB;
	Piece enPassantPiece;

	MoveLog(Board board, Move move) {
		this.move = move;
		this.whiteLeftRookMoved = board.hasWhiteLeftRookMoved();
		this.whiteRightRookMoved = board.hasWhiteRightRookMoved();
		this.whiteKingMoved = board.hasWhiteKingMoved();
		this.blackLeftRookMoved = board.hasBlackLeftRookMoved();
		this.blackRightRookMoved = board.hasBlackRightRookMoved();
		this.blackKingMoved = board.hasBlackKingMoved();
		this.drawCounter = board.drawCounter();
		this.moveCounter = board.moveCounter();
		this.enPassant = board.enPassant(move.to.file);
	}
}
