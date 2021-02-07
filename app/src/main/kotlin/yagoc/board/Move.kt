package yagoc.board

import yagoc.pieces.Piece
import yagoc.pieces.PieceColor
import yagoc.pieces.PieceType
import yagoc.pieces.none
import java.io.Serializable
import kotlin.math.abs

class Move(private val fromPiece: Piece, private val from: Square, private val to: Square) : Serializable {
    override fun toString(): String {
        return fromPiece.toString() + " " + RANK_NAMES[from.rank()] + FILE_NAMES[from.file()] + " " + RANK_NAMES[to.rank()] + FILE_NAMES[to.file()]
    }

    /**
     * positive if going ahead, negative if going backwards
     */
    fun rankDistance(): Int {
        return (to.rank() - from.rank()) * if (fromPiece.color() == PieceColor.whiteSet) -1 else 1
    }

    /**
     * positive if going to the right, negative if going to the left
     */
    fun fileDistance(): Int {
        return (to.file() - from.file()) * if (fromPiece.color() == PieceColor.whiteSet) 1 else -1
    }

    fun enPassantSquare(): Square {
        return this.to().previousRank(fromPiece().color())
    }

    fun fileDistanceAbs(): Int {
        return abs(fileDistance())
    }

    fun rankDistanceAbs(): Int {
        return abs(rankDistance())
    }

    fun hasSameFile(): Boolean {
        return to.file() == from.file()
    }

    fun hasSameRank(): Boolean {
        return from.rank() == to.rank()
    }

    val isCastling: Boolean = fromPiece.pieceType() == PieceType.King && fileDistanceAbs() == 2 && rankDistance() == 0
    val isCastlingQueenside: Boolean = isCastling && to.file() < from.file()

    fun fromPiece(): Piece {
        return fromPiece
    }

    fun from(): Square {
        return from
    }

    fun to(): Square {
        return to
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