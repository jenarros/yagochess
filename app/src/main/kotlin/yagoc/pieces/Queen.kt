package yagoc.pieces

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.Square
import yagoc.pieces.Rook
import java.util.stream.Stream

class Queen(pieceColor: PieceColor) : Piece(PieceType.Queen, pieceColor) {
    public override fun isValidForPiece(board: BoardView, move: Move): Boolean {
        return Bishop.isCorrectMoveForBishop(board, move) || Rook.Companion.isCorrectMoveForRook(board, move)
    }

    public override fun generateMovesForPiece(board: BoardView, from: Square): Stream<Move> {
        return Stream.concat(
            Bishop.generateMovesForBishop(board, from),
            Rook.generateMovesForRook(board, from)
        )
    }
}