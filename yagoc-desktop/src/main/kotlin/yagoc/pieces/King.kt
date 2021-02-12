package yagoc.pieces

import yagoc.board.*
import yagoc.board.BoardRules.isInCheck
import yagoc.board.BoardRules.moveDoesNotCreateCheck
import java.util.stream.Stream

class King(pieceColor: PieceColor) : Piece(PieceType.King, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move) =
        move.rankDistanceAbs() <= 1 && (move.fileDistanceAbs() <= 1 || isCorrectCastling(board, move))

    public override fun generateMovesForPiece(board: BoardView, from: Square) =
        board.pieceAt(from).let { piece ->
            Stream.of(
                from.next2File(piece.color),
                from.previous2File(piece.color),
                from.nextRank(piece.color),
                from.nextRank(piece.color).previousFile(piece.color),
                from.nextRank(piece.color).nextFile(piece.color),
                from.previousRank(piece.color),
                from.previousRank(piece.color).nextFile(piece.color),
                from.previousRank(piece.color).previousFile(piece.color),
                from.nextFile(piece.color),
                from.previousFile(piece.color)
            ).filter { obj: Square -> obj.exists() }
                .map { to: Square -> Move(piece, from, to) }
        }

    private fun isCorrectCastling(board: BoardView, move: Move): Boolean {
        return when {
            move.from == e8Square && !board.hasBlackKingMoved() -> when {
                move.to == c8Square && board.pieceAt(a8Square) == blackRook && !board.hasBlackLeftRookMoved() ->
                    // black set, queenside
                    board.noneAt(b8Square) &&
                            board.noneAt(c8Square) &&
                            board.noneAt(d8Square) &&
                            moveDoesNotCreateCheck(board, move.from, d8Square) &&
                            moveDoesNotCreateCheck(board, move.from, c8Square)
                // black set, blackside
                move.to == g8Square && board.pieceAt(h8Square) == blackRook && !board.hasBlackLeftRookMoved() ->
                    board.noneAt(f8Square) &&
                            board.noneAt(g8Square) &&
                            moveDoesNotCreateCheck(board, move.from, g8Square) &&
                            moveDoesNotCreateCheck(board, move.from, f8Square)
                else -> false
            }
            move.from == e1Square && !board.hasWhiteKingMoved() -> when {
                // white set, queenside
                move.to == c1Square && board.pieceAt(a1Square) == whiteRook && !board.hasWhiteLeftRookMoved() ->
                    board.noneAt(b1Square) &&
                            board.noneAt(c1Square) &&
                            board.noneAt(d1Square) &&
                            moveDoesNotCreateCheck(board, move.from, d1Square) &&
                            moveDoesNotCreateCheck(board, move.from, c1Square)
                // white set, kingside
                move.to == g1Square && board.pieceAt(h1Square) == whiteRook && !board.hasWhiteRightRookMoved() ->
                    board.noneAt(f1Square) &&
                            board.noneAt(g1Square) &&
                            moveDoesNotCreateCheck(board, move.from, g1Square) &&
                            moveDoesNotCreateCheck(board, move.from, f1Square)
                else -> false
            }
            else -> false
        } && !isInCheck(board, move.fromPiece.color)
    }
}