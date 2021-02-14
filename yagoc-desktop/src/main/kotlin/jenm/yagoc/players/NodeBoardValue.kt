package jenm.yagoc.players

import jenm.yagoc.board.Move


sealed class BoardValue {
    abstract val value: Int
}

data class NodeBoardValue(val move: Move, override val value: Int) : BoardValue()
data class LeafBoardValue(override val value: Int) : BoardValue()

fun min(left: BoardValue, right: BoardValue) = if (left.value < right.value) left else right
fun max(left: BoardValue, right: BoardValue) = if (left.value > right.value) left else right