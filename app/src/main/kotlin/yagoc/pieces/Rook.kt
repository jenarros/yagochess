package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.util.stream.Stream

class Rook(pieceColor: PieceColor) : Piece(PieceType.Rook, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return isCorrectMoveForRook(board, move)
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        return generateMovesForRook(board, from)
    }

    companion object {
        fun isCorrectMoveForRook(board: BoardView, move: Move): Boolean {
            return if (move.hasSameRank()) {
                //movimiento horizontal
                var mi = Math.min(move.from().file(), move.to().file()) + 1
                val ma = Math.max(move.from().file(), move.to().file())
                while (mi < ma) {
                    if (board.someAt(Square(move.from().rank(), mi))) return false
                    mi++
                }
                true
            } else if (move.hasSameFile()) {
                //movimiento vertical
                var mi = Math.min(move.from().rank(), move.to().rank()) + 1
                val ma = Math.max(move.from().rank(), move.to().rank())
                while (mi < ma) {
                    if (board.someAt(Square(mi, move.from().file()))) return false
                    mi++
                }
                true
            } else false
        }

        fun generateMovesForRook(board: BoardView, from: Square): Stream<Move> {
            val piece = board.pieceAt(from)
            return from.straightSquares().stream()
                .map { to: Square -> Move(piece, from, to) }
        }
    }
}