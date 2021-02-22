package jenm.yagoc.pieces

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.board.Move.Companion.move
import jenm.yagoc.board.Square
import java.util.stream.Stream

class Knight(pieceColor: PieceColor, variant: PieceVariant) : Piece(PieceType.Knight, pieceColor, variant) {
    public override fun isValidForPiece(board: BoardView, move: Move) =
        (move.rankDistanceAbs() == 2 && move.fileDistanceAbs() == 1) ||
                (move.fileDistanceAbs() == 2 && move.rankDistanceAbs() == 1)

    public override fun generateMovesForPiece(board: BoardView, from: Square) =
        board.pieceAt(from).let { piece ->
            Stream.of(
                from.next2Rank(piece.color).nextFile(piece.color),
                from.next2Rank(piece.color).previousFile(piece.color),
                from.previous2Rank(piece.color).nextFile(piece.color),
                from.previous2Rank(piece.color).previousFile(piece.color),
                from.next2File(piece.color).nextRank(piece.color),
                from.next2File(piece.color).previousRank(piece.color),
                from.previous2File(piece.color).nextRank(piece.color),
                from.previous2File(piece.color).previousRank(piece.color)
            ).filter { obj: Square -> obj.exists() }
                .map { to: Square -> move(board, from, to) }
        }
}