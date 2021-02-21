package jenm.yagoc

import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.players.MinimaxPlayer
import jenm.yagoc.players.Player
import jenm.yagoc.players.UserPlayer

data class YagocSettings(val blackPlayer: Player, val whitePlayer: Player)

const val defaultLevel = 4

val defaultSettings = YagocSettings(
    MinimaxPlayer(PieceColor.BlackSet, defaultLevel),
    UserPlayer(PieceColor.WhiteSet)
)