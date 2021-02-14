package jenm.yagoc.players

import jenm.yagoc.Yagoc
import jenm.yagoc.board.BoardRules.generateMoves
import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.pieces.PieceColor
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

class MinimaxPlayer(
    name: String,
    pieceColor: PieceColor,
    private val level: Int,
    private val strategy: PlayerStrategy
) : Player(name, pieceColor, PlayerType.Computer), Serializable {
    val processedMoveCounter = AtomicInteger(0)

    override fun toString() = pieceColor.toString() + "\t" + type + "\t" + level

    override fun move(board: BoardView): Move {
        processedMoveCounter.set(0)

        val moveValue = alphaBeta(level, board, Int.MIN_VALUE, Int.MAX_VALUE)

        Yagoc.logger.debug("processed = " + processedMoveCounter + ", minimax = " + moveValue.value)

        return (moveValue as NodeBoardValue).move
    }

    fun alphaBeta(depth: Int, board: BoardView, alfa: Int, beta: Int): BoardValue {
        processedMoveCounter.getAndIncrement()

        return if (depth == 0) {
            leafValue(board)
        } else {
            val moves = generateMoves(board)
            when {
                moves.isEmpty() -> LeafBoardValue(alphaBetaDefault(depth))
                isMaxPlayer(depth) -> alphaBetaMax(depth, board, alfa, beta, moves)
                else -> alphaBetaMin(depth, board, alfa, beta, moves)
            }
        }
    }

    /**
     * maximizing player = current player (as depth = level)
     */
    private fun isMaxPlayer(depth: Int) = (level - depth) % 2 == 0

    private fun alphaBetaDefault(depth: Int) =
        if (isMaxPlayer(depth)) Int.MIN_VALUE + (level - depth + 1)
        else Int.MAX_VALUE - (level - depth + 1)

    private fun alphaBetaMin(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        moves: Collection<Move>,
    ): BoardValue {
        var betaValue: BoardValue = NodeBoardValue(moves.first(), beta)
        for (move in moves) {
            // beta = min[beta, AlphaBeta(N_k,alpha,beta)]
            betaValue = min(
                betaValue,
                NodeBoardValue(
                    move,
                    board.playAndUndo(move) { alphaBeta(depth - 1, board, alpha, betaValue.value) }.value
                )
            )

            // beta cutoff
            if (alpha >= betaValue.value && betaValue is NodeBoardValue) {
                return NodeBoardValue(betaValue.move, alpha)
            }
        }

        return betaValue
    }

    private fun leafValue(board: BoardView) = LeafBoardValue(strategy(board, pieceColor))

    private fun alphaBetaMax(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        moves: Collection<Move>,
    ): BoardValue {
        var moveValue: BoardValue = NodeBoardValue(moves.first(), alpha)
        for (move in moves) {
            // alpha = max[alpha, AlphaBeta(N_k,alpha,beta)
            moveValue = max(
                NodeBoardValue(
                    move,
                    board.playAndUndo(move) { alphaBeta(depth - 1, board, moveValue.value, beta) }.value
                ),
                moveValue
            )

            // alpha cutoff
            if (moveValue.value >= beta && moveValue is NodeBoardValue) {
                return NodeBoardValue(moveValue.move, beta)
            }
        }

        return moveValue
    }
}