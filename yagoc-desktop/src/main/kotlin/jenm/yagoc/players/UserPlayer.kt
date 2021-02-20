package jenm.yagoc.players

import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.pieces.PieceColor
import java.io.Serializable

class UserPlayer(pieceColor: PieceColor) : Player(pieceColor, PlayerType.User), Serializable {

    override fun move(board: BoardView): Move {
        throw RuntimeException("$pieceColor requires user input.")
    }

    override fun toString() = pieceColor.toString() + "\t" + type
}