package yagoc.players

import yagoc.Yagoc
import yagoc.board.BoardRules.generateMoves
import yagoc.board.BoardView
import yagoc.board.Move
import yagoc.board.a1Square
import yagoc.pieces.PieceColor
import yagoc.pieces.blackPawn
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

class ComputerPlayer(
    name: String,
    pieceColor: PieceColor,
    private val level: Int,
    private val strategy: PlayerStrategy
) : Player(name, pieceColor, PlayerType.Computer), Serializable {
    private val noMoves = Move(blackPawn, a1Square, a1Square)

    override fun toString(): String {
        return pieceColor.toString() + "\t" + type + "\t" + level
    }

    override fun move(board: BoardView): Move {
        val moveCounter = AtomicInteger(0)
        val start = System.currentTimeMillis()
        val moveValue = alphaBeta(level, board, Int.MIN_VALUE, Int.MAX_VALUE, moveCounter)
        val elapsed = System.currentTimeMillis() - start + 1
        Yagoc.logger.info("alpha-beta: processed = " + moveCounter + " moves in " + elapsed + "ms " + moveCounter.toInt() / elapsed + " moves/ms,  minimax = " + moveValue.value)
        return moveValue.move
    }

    fun alphaBeta(depth: Int, board: BoardView, alfa: Int, beta: Int, moveCounter: AtomicInteger): MoveValue {
        return when {
            depth == 0 -> leafValue(board)
            (level - depth) % 2 == 0 -> // maximizing player = current player (as depth = level)
                alphaBetaMax(depth, board, alfa, beta, moveCounter)
            else -> alphaBetaMin(depth, board, alfa, beta, moveCounter)
        }
    }

    private fun alphaBetaMin(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        processedMoves: AtomicInteger
    ): MoveValue {
        val moves = generateMoves(board)

        // checkmate
        if (moves.isEmpty()) {
            return MoveValue(noMoves, Int.MAX_VALUE - (level - depth + 1))
        }
        var betaMoveValue: MoveValue
        var moveValue = MoveValue(moves.stream().findFirst().orElseThrow(), beta)
        for (move in moves) {
            processedMoves.getAndIncrement()

            // beta = min[beta, AlphaBeta(N_k,alpha,beta)]
            val v = moveValue.value
            betaMoveValue = board.playAndUndo(move) { alphaBeta(depth - 1, board, alpha, v, processedMoves) }
            if (betaMoveValue.value < moveValue.value) {
                moveValue = MoveValue(move, betaMoveValue.value) // better
            }

            // beta cutoff
            if (alpha >= moveValue.value) {
                return MoveValue(moveValue.move, alpha)
            }
        }

        return moveValue
    }

    private fun leafValue(board: BoardView): MoveValue {
        return MoveValue(noMoves, strategy.apply(board, pieceColor))
    }

    private fun alphaBetaMax(
        depth: Int,
        board: BoardView,
        alpha: Int,
        beta: Int,
        processedMoves: AtomicInteger
    ): MoveValue {
        val moves = generateMoves(board)

        // checkmate
        if (moves.isEmpty()) {
            return MoveValue(noMoves, Int.MIN_VALUE + (level - depth + 1))
        }
        var alphaMoveValue: MoveValue
        var moveValue = MoveValue(moves.stream().findFirst().orElseThrow(), alpha)
        for (move in moves) {
            processedMoves.getAndIncrement()

            // alpha = max[alpha, AlphaBeta(N_k,alpha,beta)
            val v = moveValue.value
            alphaMoveValue = board.playAndUndo(move) { alphaBeta(depth - 1, board, v, beta, processedMoves) }
            if (alphaMoveValue.value > moveValue.value) {
                moveValue = MoveValue(move, alphaMoveValue.value) // better
            }

            // alpha cutoff
            if (moveValue.value >= beta) {
                return MoveValue(moveValue.move, beta)
            }
        }

        return moveValue
    }
}