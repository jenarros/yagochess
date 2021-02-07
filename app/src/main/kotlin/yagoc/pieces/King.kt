package yagoc.pieces

import yagoc.board.*
import yagoc.board.BoardRules.isInCheck
import yagoc.board.BoardRules.moveDoesNotCreateCheck
import java.util.stream.Stream

class King(pieceColor: PieceColor) : Piece(PieceType.King, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return move.fileDistanceAbs() <= 1 && move.rankDistanceAbs() <= 1 || isCorrectCastling(board, move)
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        val piece = board.pieceAt(from)
        return Stream.of(
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
        if ((move.from().rank == 7 && move.fromPiece() == whiteKing && !board.hasWhiteKingMoved() ||
                    move.from().rank == 0 && move.fromPiece() == blackKing && !board.hasBlackKingMoved()) &&
            move.hasSameRank() && !isInCheck(board, move.fromPiece().color)
        ) {
            if (move.to().file == 2 && board.pieceAt(square(move.from().rank, 0)) == move.fromPiece()
                    .switchTo(PieceType.Rook)
            ) { // queenside
                // white set
                return if (move.fromPiece().color == PieceColor.whiteSet && !board.hasWhiteLeftRookMoved() &&
                    board.noneAt(b1Square) && board.noneAt(c1Square) &&
                    board.noneAt(d1Square) &&
                    moveDoesNotCreateCheck(board, move.from(), d1Square) &&
                    moveDoesNotCreateCheck(board, move.from(), c1Square)
                ) {
                    true
                } else move.fromPiece()
                    .color == PieceColor.blackSet && !board.hasBlackLeftRookMoved() &&
                        board.noneAt(b8Square) && board.noneAt(c8Square) &&
                        board.noneAt(d8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), d8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), c8Square)
                // black set
            } else if (move.to().file == 6 && board.pieceAt(square(move.from().rank, 7)) == move.fromPiece()
                    .switchTo(PieceType.Rook)
            ) { // kingside
                // white set
                return if (move.fromPiece().color == PieceColor.whiteSet && !board.hasWhiteRightRookMoved() &&
                    board.noneAt(f1Square) && board.noneAt(g1Square) &&
                    moveDoesNotCreateCheck(board, move.from(), g1Square) &&
                    moveDoesNotCreateCheck(board, move.from(), f1Square)
                ) {
                    true
                } else move.fromPiece().color == PieceColor.blackSet && !board.hasBlackRightRookMoved() &&
                        board.noneAt(f8Square) && board.noneAt(g8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), g8Square) &&
                        moveDoesNotCreateCheck(board, move.from(), f8Square)
                // black set
            }
        }
        return false
    }
}