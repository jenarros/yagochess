package yagoc;

import yagoc.pieces.Piece;

public class MoveLog {
	final Move move;
	final boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
	final boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
	final int enPassant;
	final int drawCounter;
	final int moveCounter;
	final Piece toPiece;

	MoveType type;
	Move castlingExtraMove;
	Piece enPassantPiece;

	public MoveLog(Board board, Move move, Piece toPiece) {
		this.move = move;
		this.toPiece = toPiece;
		this.whiteLeftRookMoved = board.hasWhiteLeftRookMoved();
		this.whiteRightRookMoved = board.hasWhiteRightRookMoved();
		this.whiteKingMoved = board.hasWhiteKingMoved();
		this.blackLeftRookMoved = board.hasBlackLeftRookMoved();
		this.blackRightRookMoved = board.hasBlackRightRookMoved();
		this.blackKingMoved = board.hasBlackKingMoved();
		this.drawCounter = board.drawCounter();
		this.moveCounter = board.moveCounter();
		this.enPassant = board.enPassant(move.to().file());
	}
}
