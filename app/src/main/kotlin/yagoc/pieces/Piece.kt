package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.io.Serializable
import java.util.*
import java.util.stream.Stream

abstract class Piece(val pieceType: PieceType, val color: PieceColor) : Serializable {

    fun switchTo(type: PieceType): Piece {
        return all.stream()
            .filter { piece: Piece -> type == piece.pieceType && piece.color == color }
            .findFirst().orElseThrow()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val piece = o as Piece
        return pieceType == piece.pieceType &&
                color == piece.color
    }

    override fun hashCode(): Int {
        return Objects.hash(pieceType, color)
    }

    override fun toString(): String {
        return when (pieceType) {
            PieceType.none -> {
                ""
            }
            PieceType.Knight -> {
                "N"
            }
            else -> {
                pieceType.name.substring(0, 1)
            }
        }
    }

    /**
     * Can safely assume that general rules have been validated such as:
     * - the piece is not moving to a square with a piece of the same color
     */
    protected abstract fun isValidForPiece(board: BoardView, move: Move): Boolean

    fun isCorrectMove(board: BoardView, move: Move): Boolean {
        return if (board.pieceAt(move.from()).color == board.pieceAt(move.to()).color) {
            false
        } else isValidForPiece(board, move)
    }

    /**
     * These are all potential moves pre-validation, the resulting moves need to be validated.
     */
    protected abstract fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move>

    fun generateMoves(board: BoardView, from: Square): Stream<Move> {
        return generateMovesForPiece(board, from).filter { move: Move -> isCorrectMove(board, move) }
    }

    fun notOfSameColor(pieceColor: PieceColor): Boolean {
        return this !== none && color != pieceColor
    }

    fun toUniqueChar(): Char {
        if (PieceColor.blackSet == color) {
            return toString()[0]
        } else if (PieceColor.whiteSet == color) {
            return toString().toLowerCase()[0]
        }
        return '-'
    }
}