package jenm.yagoc.board

import jenm.yagoc.pieces.Piece
import java.io.Serializable

class MoveLog private constructor(
    board: BoardView,
    val move: Move,
    val toPiece: Piece,
    val type: MoveType,
    val castlingExtraMove: Move? = null
) : Serializable {

    val whiteLeftRookMoved = board.hasWhiteLeftRookMoved()

    val whiteRightRookMoved = board.hasWhiteRightRookMoved()

    val whiteKingMoved = board.hasWhiteKingMoved()

    val blackLeftRookMoved = board.hasBlackLeftRookMoved()

    val blackRightRookMoved = board.hasBlackRightRookMoved()

    val blackKingMoved = board.hasBlackKingMoved()

    val enPassant = board.enPassant(move.to.file)

    val drawCounter = board.drawCounter()

    val moveCounter = board.moveCounter()

    val enPassantPiece: Piece? = if (type == MoveType.EnPassant) board.pieceAt(move.enPassantSquare()) else null


    companion object {
        @JvmStatic
        fun enPassant(board: BoardView, move: Move, toPiece: Piece): MoveLog {
            return MoveLog(board, move, toPiece, MoveType.EnPassant)
        }

        @JvmStatic
        fun castling(board: BoardView, move: Move, toPiece: Piece, castlingExtraMove: Move?): MoveLog {
            return MoveLog(board, move, toPiece, MoveType.Castling, castlingExtraMove)
        }

        @JvmStatic
        fun normalMove(board: BoardView, move: Move, toPiece: Piece): MoveLog {
            return MoveLog(board, move, toPiece, MoveType.Normal)
        }
    }

    override fun toString(): String {
        return "$move"
    }
}