package jenm.yagoc.players

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.pieces.PieceColor

abstract class Player(val name: String, val pieceColor: PieceColor, val type: PlayerType) {
    abstract fun move(board: BoardView): Move
    val isUser = type == PlayerType.User
    val isComputer = type == PlayerType.Computer
}