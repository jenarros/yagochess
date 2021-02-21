package jenm.yagoc.board

import jenm.yagoc.pieces.Piece
import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.pieces.PieceType
import jenm.yagoc.pieces.none
import java.io.Serializable
import kotlin.math.abs

data class Move(val fromPiece: Piece, val from: Square, val to: Square) : Serializable {
    /**
     * positive if going ahead, negative if going backwards
     */
    fun rankDistance(): Int {
        return (to.rank - from.rank) * if (fromPiece.color == PieceColor.WhiteSet) -1 else 1
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    fun fileDistance(): Int {
        return (to.file - from.file) * if (fromPiece.color == PieceColor.WhiteSet) 1 else -1
    }

    fun enPassantSquare(): Square {
        return this.to.previousRank(fromPiece.color)
    }

    fun fileDistanceAbs(): Int {
        return abs(fileDistance())
    }

    fun rankDistanceAbs(): Int {
        return abs(rankDistance())
    }

    fun hasSameFile(): Boolean {
        return to.file == from.file
    }

    fun hasSameRank(): Boolean {
        return from.rank == to.rank
    }

    val isCastling = fromPiece.pieceType == PieceType.King && fileDistanceAbs() == 2 && rankDistance() == 0

    val isCastlingQueenside = isCastling && to.file < from.file

    override fun toString(): String {
        return fromPiece.toString() + " " + FILE_NAMES[from.file] + RANK_NAMES[from.rank] + " " + FILE_NAMES[to.file] + RANK_NAMES[to.rank]
    }

    companion object {
        @JvmField
        val FILE_NAMES = arrayOf("a", "b", "c", "d", "e", "f", "g", "h")

        @JvmField
        val RANK_NAMES = arrayOf("8", "7", "6", "5", "4", "3", "2", "1")
    }

    init {
        require(fromPiece != none) { "Cannot move nothing" }
        require(from.exists() && to.exists()) { "This move contains non-existing squares: $from $to" }
    }
}