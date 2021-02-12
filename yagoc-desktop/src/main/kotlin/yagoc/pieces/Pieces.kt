package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.util.stream.Stream

@JvmField
val blackRook: Piece = Rook(PieceColor.BlackSet)

@JvmField
val blackKnight: Piece = Knight(PieceColor.BlackSet)

@JvmField
val blackBishop: Piece = Bishop(PieceColor.BlackSet)

@JvmField
val blackQueen: Piece = Queen(PieceColor.BlackSet)

@JvmField
val blackKing: Piece = King(PieceColor.BlackSet)

@JvmField
val blackPawn: Piece = Pawn(PieceColor.BlackSet)

@JvmField
val whiteRook: Piece = Rook(PieceColor.WhiteSet)

@JvmField
val whiteKnight: Piece = Knight(PieceColor.WhiteSet)

@JvmField
val whiteBishop: Piece = Bishop(PieceColor.WhiteSet)

@JvmField
val whiteQueen: Piece = Queen(PieceColor.WhiteSet)

@JvmField
val whiteKing: Piece = King(PieceColor.WhiteSet)

@JvmField
val whitePawn: Piece = Pawn(PieceColor.WhiteSet)

@JvmField
val none: Piece = object : Piece(PieceType.None, PieceColor.None) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        throw RuntimeException("Should not happen")
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        throw RuntimeException("Should not happen")
    }
}

object Pieces {
    fun parse(c: Char): Piece {
        return when (c) {
            'p' -> whitePawn
            'r' -> whiteRook
            'n' -> whiteKnight
            'b' -> whiteBishop
            'q' -> whiteQueen
            'k' -> whiteKing
            'P' -> blackPawn
            'R' -> blackRook
            'N' -> blackKnight
            'B' -> blackBishop
            'Q' -> blackQueen
            'K' -> blackKing
            else -> none
        }
    }
}