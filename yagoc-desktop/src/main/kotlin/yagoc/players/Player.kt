package yagoc.players

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.pieces.PieceColor

abstract class Player(val name: String, val pieceColor: PieceColor, val type: PlayerType) {
    abstract fun move(board: BoardView): Move
    val isUser = type == PlayerType.User
    val isComputer = type == PlayerType.Computer
}