package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import yagoc.board.square
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min

class Rook(pieceColor: PieceColor) : Piece(PieceType.Rook, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return isCorrectMoveForRook(board, move)
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        return generateMovesForRook(board, from)
    }

    companion object {
        @JvmStatic
        fun isCorrectMoveForRook(board: BoardView, move: Move): Boolean {
            return when {
                move.hasSameRank() -> {
                    // move horizontally
                    var mi = min(move.from().file, move.to().file) + 1
                    val ma = max(move.from().file, move.to().file)
                    while (mi < ma) {
                        if (board.someAt(square(move.from().rank, mi))) return false
                        mi++
                    }
                    true
                }
                move.hasSameFile() -> {
                    // move vertically
                    var mi = min(move.from().rank, move.to().rank) + 1
                    val ma = max(move.from().rank, move.to().rank)
                    while (mi < ma) {
                        if (board.someAt(square(mi, move.from().file))) return false
                        mi++
                    }
                    true
                }
                else -> false
            }
        }

        @JvmStatic
        fun generateMovesForRook(board: BoardView, from: Square): Stream<Move> {
            val piece = board.pieceAt(from)
            return from.straightSquares().stream()
                .map { to: Square -> Move(piece, from, to) }
        }
    }
}