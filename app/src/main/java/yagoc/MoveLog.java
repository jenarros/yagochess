package yagoc;

import yagoc.pieces.Piece;

import java.io.Serializable;

public class MoveLog implements Serializable {
	final Move move;
	final boolean whiteLeftRookMoved, whiteRightRookMoved, whiteKingMoved;
	final boolean blackLeftRookMoved, blackRightRookMoved, blackKingMoved;
	final int enPassant;
	final int drawCounter;
	final int moveCounter;
	final Piece toPiece;

	final MoveType type;
	final Move castlingExtraMove;
	final Piece enPassantPiece;

	private MoveLog(Board board, Move move, Piece toPiece, MoveType moveType, Move castlingExtraMove) {
		this.type = moveType;
		this.move = move;
		this.toPiece = toPiece;
		this.enPassantPiece = moveType.equals(MoveType.enPassant) ? board.pieceAt(move.enPassantSquare()) : null;
		this.castlingExtraMove = castlingExtraMove;
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

	public static MoveLog enPassant(Board board, Move move, Piece toPiece) {
		return new MoveLog(board, move, toPiece, MoveType.enPassant, null);
	}

	public static MoveLog castling(Board board, Move move, Piece toPiece, Move castlingExtraMove) {
		return new MoveLog(board, move, toPiece, MoveType.castling, castlingExtraMove);
	}

	public static MoveLog normalMove(Board board, Move move, Piece toPiece) {
		return new MoveLog(board, move, toPiece, MoveType.normal, null);
	}
}
