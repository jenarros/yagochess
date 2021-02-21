package jenm.yagoc.pieces

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.board.Square
import java.util.stream.Stream

@JvmField
val blackRook: Piece = Rook(PieceColor.BlackSet)

@JvmField
val blackKnightKingside: Piece = Knight(PieceColor.BlackSet, PieceVariant.Kingside)

@JvmField
val blackKnightQueenside: Piece = Knight(PieceColor.BlackSet, PieceVariant.Queenside)

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
val whiteKnightKingside: Piece = Knight(PieceColor.WhiteSet, PieceVariant.Kingside)

@JvmField
val whiteKnightQueenside: Piece = Knight(PieceColor.WhiteSet, PieceVariant.Queenside)

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
            'n' -> whiteKnightKingside
            'b' -> whiteBishop
            'q' -> whiteQueen
            'k' -> whiteKing
            'P' -> blackPawn
            'R' -> blackRook
            'N' -> blackKnightKingside
            'B' -> blackBishop
            'Q' -> blackQueen
            'K' -> blackKing
            else -> none
        }
    }
}