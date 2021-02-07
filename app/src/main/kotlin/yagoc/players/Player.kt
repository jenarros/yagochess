package yagoc.players

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.pieces.PieceColor

interface Player {
    fun move(board: BoardView): Move
    fun type(): PlayerType
    fun pieceColor(): PieceColor
    fun name(): String
    val isUser: Boolean
        get() = type() == PlayerType.User
    val isComputer: Boolean
        get() = type() == PlayerType.Computer
}