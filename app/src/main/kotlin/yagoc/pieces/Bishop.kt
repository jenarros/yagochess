package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.util.stream.Stream
import kotlin.math.min

class Bishop(pieceColor: PieceColor) : Piece(PieceType.Bishop, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return isCorrectMoveForBishop(board, move)
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        return generateMovesForBishop(board, from)
    }

    companion object {
        fun generateMovesForBishop(board: BoardView, from: Square): Stream<Move> {
            val piece = board.pieceAt(from)
            return from.diagonalSquares().stream().map { to: Square -> Move(piece, from, to) }
        }

        fun isCorrectMoveForBishop(board: BoardView, move: Move): Boolean {
            return if (move.rankDistanceAbs() == move.fileDistanceAbs()) {
                // walk the move from left to right
                val ma = Math.max(move.from().file(), move.to().file()) //y final
                var rank: Int
                var file: Int

                // starting position
                if (move.from().file() < move.to().file()) {
                    rank = move.from().rank()
                    file = move.from().file()
                } else {
                    rank = move.to().rank()
                    file = move.to().file()
                }

                val direction = if (rank == min(move.from().rank(), move.to().rank())) { // down
                    1
                } else { // up
                    -1
                }

                // go through the squares
                file++
                rank += direction
                while (file < ma) {
                    if (board.someAt(Square(rank, file))) return false
                    rank += direction
                    file++
                }
                true
            } else {
                false
            }
        }
    }
}