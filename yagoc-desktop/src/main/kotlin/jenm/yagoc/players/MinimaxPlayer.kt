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

        Yagoc.logger.info("processed = " + processedMoveCounter + ", minimax = " + moveValue.value)

        return if (moveValue is NodeBoardValue) moveValue.move else throw RuntimeException("There are no more moves")
    }

    fun alphaBeta(depth: Int, board: BoardView, alfa: Int, beta: Int): BoardValue {
        processedMoveCounter.getAndIncrement()

        return if (depth == 0) {
            leafValue(board)
        } else {
            val moves = generateMoves(board)

            when {
                moves.isEmpty() -> LeafBoardValue(alphaBetaDefault(depth))
                isMaxPlayer(depth) -> alphaBetaMax(depth, board, alfa, beta, moves.iterator())
                else -> alphaBetaMin(depth, board, alfa, beta, moves.iterator())
            }
        }
    }

    private fun alphaBetaDefault(depth: Int) = if (isMaxPlayer(depth)) Int.MIN_VALUE else Int.MAX_VALUE

    /**
     * maximizing player = current player (as depth = level)
     */
    private fun isMaxPlayer(depth: Int) = (level - depth) % 2 == 0

    private fun alphaBetaMin(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        moves: Iterator<Move>
    ): BoardValue {
        var betaValue: BoardValue = LeafBoardValue(beta)

        do {
            val move = moves.next()
            // beta = min[beta, AlphaBeta(N_k,alpha,beta)]
            betaValue = min(
                betaValue,
                board.playAndUndo(move) { alphaBeta(depth - 1, board, alpha, betaValue.value) }
            )

            // beta cutoff
            if (alpha >= betaValue.value) {
                return NodeBoardValue(move, alpha)
            }
        } while (moves.hasNext())

        return betaValue
    }

    private fun leafValue(board: BoardView) = LeafBoardValue(strategy(board, pieceColor))

    private fun alphaBetaMax(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        moves: Iterator<Move>
    ): BoardValue {
        var alphaValue: BoardValue = LeafBoardValue(alpha)

        do {
            val move = moves.next()
            // alpha = max[alpha, AlphaBeta(N_k,alpha,beta)
            alphaValue = max(
                alphaValue,
                board.playAndUndo(move) { alphaBeta(depth - 1, board, alphaValue.value, beta) }
            )

            // alpha cutoff
            if (alphaValue.value >= beta) {
                return NodeBoardValue(move, beta)
            }
        } while (moves.hasNext())

        return alphaValue
    }
}