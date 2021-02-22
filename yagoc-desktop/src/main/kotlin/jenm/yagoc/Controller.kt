package jenm.yagoc

import jenm.yagoc.Yagoc.logger
import jenm.yagoc.board.Board
import jenm.yagoc.board.BoardRules.isCorrectMove
import jenm.yagoc.board.BoardRules.moveDoesNotCreateCheck
import jenm.yagoc.board.BoardRules.noMoreMovesAllowed
import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Move
import jenm.yagoc.board.Square
import jenm.yagoc.pieces.blackPawn
import jenm.yagoc.pieces.blackQueen
import jenm.yagoc.pieces.whitePawn
import jenm.yagoc.pieces.whiteQueen
import jenm.yagoc.ui.UIAdapter
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class Controller(private val board: Board, private val uiAdapter: UIAdapter) {
    private val checkpoints = ArrayList<Board>()
    private var finished = false
    private var paused = false
    private var currentBoardView = Board(board)

    fun resetBoard(board: Board) {
        checkpoints.clear()
        this.board.resetWith(board)
        nextMove()
    }

    fun undo() {
        if (checkpoints.isNotEmpty()) {
            board.resetWith(checkpoints.removeAt(checkpoints.size - 1))
            finished = false
            uiAdapter.invokeLater { nextMove() }
        }
    }

    fun saveBoard(absolutePath: String) {
        FileOutputStream(absolutePath).use { fileStream ->
            ObjectOutputStream(fileStream).use { stream -> stream.writeObject(board) }
        }
    }

    fun move(from: Square, to: Square) {
        val copy = Board(board)
        if (moveIfPossible(from, to)) {
            checkpoints.add(copy)
        }
        uiAdapter.invokeLater { nextMove() }
    }

    fun currentBoardView(): BoardView = currentBoardView

    private fun updateCurrentBoardView() {
        currentBoardView = Board(board)
    }

    fun moveIfPossible(from: Square, to: Square) =
        when {
            finished || paused || !board.isPieceOfCurrentPlayer(board.pieceAt(from)) -> false
            board.currentPlayer().isUser -> {
                Move(board.pieceAt(from), from, to).let { move ->
                    if (from != to && isCorrectMove(board, move) && moveDoesNotCreateCheck(board, move)) {
                        board.play(move)
                        ifPawnHasReachedFinalRankReplaceWithQueen(board, move)
                        updateCurrentBoardView()
                        finished = noMoreMovesAllowed(board)
                        logger.info(move.toString())
                        true
                    } else {
                        false
                    }
                }
            }
            else -> false
        }

    private fun ifPawnHasReachedFinalRankReplaceWithQueen(board: Board, move: Move) {
        if (move.fromPiece == blackPawn && move.to.rank == 7) {
            board.pieceAt(move.to, blackQueen)
        } else if (move.fromPiece == whitePawn && move.to.rank == 0) {
            board.pieceAt(move.to, whiteQueen)
        }
    }

    private fun nextMove() {
        if (finished || paused) return
        else if (board.currentPlayer().isComputer) {
            val checkpoint = Board(board)
            val move = board.currentPlayer().move(board)
            board.play(move)
            logger.info(move.toString())
            ifPawnHasReachedFinalRankReplaceWithQueen(board, move)
            updateCurrentBoardView()
            finished = noMoreMovesAllowed(board)
            checkpoints.add(checkpoint)

            if (board.currentPlayer().isComputer) {
                breath()
                uiAdapter.invokeLater { nextMove() }
            }
        }
    }

    private fun breath() {
        TimeUnit.MILLISECONDS.sleep(COMPUTER_PAUSE_MILLISECONDS.toLong())
    }

    fun newBoard() {
        checkpoints.clear()
        board.reset(defaultSettings)
    }

    fun configurePlayers(yagocSettings: YagocSettings) {
        uiAdapter.invokeLater {
            newBoard()
            board.blackPlayer(yagocSettings.blackPlayer)
            board.whitePlayer(yagocSettings.whitePlayer)
            logger.info("name\ttype")
            logger.info(board.blackPlayer().toString())
            logger.info(board.whitePlayer().toString())

            if (board.currentPlayer().isComputer) {
                resume()
            }
        }
    }

    fun togglePause() {
        paused = !paused
        logger.info(if (paused) "Game paused" else "Game resumed")
        if (!paused) {
            uiAdapter.invokeLater { nextMove() }
        }
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        uiAdapter.invokeLater {
            paused = false
            nextMove()
        }
    }

    companion object {
        private const val COMPUTER_PAUSE_MILLISECONDS = 1000
    }
}