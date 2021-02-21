package jenm.yagoc.pieces

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.board.Square
import java.io.Serializable
import java.util.*
import java.util.stream.Stream

abstract class Piece(val pieceType: PieceType, val color: PieceColor, val variant: PieceVariant = PieceVariant.none) :
    Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val piece = other as Piece
        return pieceType == piece.pieceType &&
                color == piece.color &&
                variant == piece.variant
    }

    override fun hashCode() = Objects.hash(pieceType, color, variant)

    override fun toString() = when (pieceType) {
        PieceType.None -> ""
        PieceType.Knight -> "N"
        else -> pieceType.name.substring(0, 1)
    }

    /**
     * Can safely assume that general rules have been validated such as:
     * - the piece is not moving to a square with a piece of the same color
     */
    protected abstract fun isValidForPiece(board: BoardView, move: Move): Boolean

    fun isCorrectMove(board: BoardView, move: Move) =
        board.pieceAt(move.from).notOfSameColor(board.pieceAt(move.to).color) &&
                isValidForPiece(board, move)

    /**
     * These are all potential moves pre-validation, the resulting moves need to be validated.
     */
    protected abstract fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move>

    fun generateMoves(board: BoardView, from: Square): Stream<Move> {
        return generateMovesForPiece(board, from).filter { move: Move -> isCorrectMove(board, move) }
    }

    fun notOfSameColor(pieceColor: PieceColor) = this !== none && color != pieceColor

    fun toUniqueChar() =
        when {
            PieceColor.BlackSet == color -> toString()[0]
            PieceColor.WhiteSet == color -> toString().toLowerCase()[0]
            else -> '-'
        }
}