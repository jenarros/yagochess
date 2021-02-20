package jenm.yagoc

import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.players.MinimaxPlayer
import jenm.yagoc.players.Player
import jenm.yagoc.players.PlayerStrategy
import jenm.yagoc.players.UserPlayer

data class YagocSettings(val blackPlayer: Player, val whitePlayer: Player)

val defaultSettings = YagocSettings(
    MinimaxPlayer(PieceColor.BlackSet, 3, PlayerStrategy.F1),
    UserPlayer(PieceColor.WhiteSet)
)