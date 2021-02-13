package jenm.yagoc.players

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.pieces.PieceColor
import java.io.Serializable

class UserPlayer(name: String, pieceColor: PieceColor) : Player(name, pieceColor, PlayerType.User), Serializable {

    override fun move(board: BoardView): Move {
        throw RuntimeException("$name cannot move without user input.")
    }

    override fun toString() = pieceColor.toString() + "\t" + name + "\t" + type
}