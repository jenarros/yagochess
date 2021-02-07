package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import java.util.stream.Stream

class Knight(pieceColor: PieceColor) : Piece(PieceType.Knight, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return if (move.rankDistanceAbs() == 2 && move.fileDistanceAbs() == 1) {
            true
        } else {
            move.fileDistanceAbs() == 2 && move.rankDistanceAbs() == 1
        }
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        val piece = board.pieceAt(from)
        return Stream.of(
            from.next2Rank(piece.color()).nextFile(piece.color()),
            from.next2Rank(piece.color()).previousFile(piece.color()),
            from.previous2Rank(piece.color()).nextFile(piece.color()),
            from.previous2Rank(piece.color()).previousFile(piece.color()),
            from.next2File(piece.color()).nextRank(piece.color()),
            from.next2File(piece.color()).previousRank(piece.color()),
            from.previous2File(piece.color()).nextRank(piece.color()),
            from.previous2File(piece.color()).previousRank(piece.color())
        ).filter { obj: Square -> obj.exists() }
            .map { to: Square -> Move(piece, from, to) }
    }
}