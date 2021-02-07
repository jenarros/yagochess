package yagoc.players

import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.pieces.PieceColor
import java.io.Serializable

class UserPlayer(private val name: String, private val pieceColor: PieceColor) : Player, Serializable {
    override fun pieceColor(): PieceColor {
        return pieceColor
    }

    override fun move(board: BoardView): Move {
        throw RuntimeException("$name cannot move without user input.")
    }

    override fun type(): PlayerType {
        return PlayerType.User
    }

    override fun name(): String {
        return name
    }

    override fun toString(): String {
        return pieceColor.toString() + "\t" + name() + "\t" + type()
    }
}