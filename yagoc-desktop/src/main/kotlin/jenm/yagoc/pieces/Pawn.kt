package jenm.yagoc.pieces

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.board.Move.Companion.move
import jenm.yagoc.board.Square
import java.util.stream.Stream

class Pawn(pieceColor: PieceColor) : Piece(PieceType.Pawn, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        if (move.rankDistance() != 1 && move.rankDistance() != 2) {
            return false
        }

        // straight ahead
        if (move.hasSameFile() && board.pieceAt(move.to) == none) {
            // if we move two squares, we should start from the initial position and next rank should be empty
            if (move.rankDistance() == 2 &&
                board.pieceAt(move.to.previousRank(move.fromPiece.color)) == none &&
                ((move.from.rank == 6 && move.fromPiece.color == PieceColor.WhiteSet) ||
                        (move.from.rank == 1 && move.fromPiece.color == PieceColor.BlackSet))
            ) return true
            if (move.rankDistance() == 1) return true
        }

        // diagonal
        return if (move.fileDistanceAbs() == 1 && move.rankDistance() == 1) {
            // capture
            if (board.pieceAt(move.to).notOfSameColor(move.fromPiece.color)) {
                true
            } else {
                board.pieceAt(move.to) == none &&
                        board.enPassant(move.to.file) == board.moveCounter() - 1 &&
                        move.from.rank == if (move.fromPiece.color == PieceColor.WhiteSet) 3 else 4
            }
        } else false
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square) =
        board.pieceAt(from).let { piece ->
            Stream.of(
                from.nextRankPreviousFile(piece.color),  // left
                from.nextRank(piece.color),  // ahead
                from.next2Rank(piece.color),  // ahead 2
                from.nextRankNextFile(piece.color) // right
            ).filter { obj: Square -> obj.exists() }
                .map { to: Square -> move(board, from, to) }
        }
}