package yagoc.players

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.pieces.PieceColor
import java.io.Serializable

class UserPlayer(name: String, pieceColor: PieceColor) : Player(name, pieceColor, PlayerType.User), Serializable {

    override fun move(board: BoardView): Move {
        throw RuntimeException("$name cannot move without user input.")
    }

    override fun toString(): String {
        return pieceColor.toString() + "\t" + name + "\t" + type
    }
}