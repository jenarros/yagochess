package jenm.yagoc

import jenm.yagoc.Yagoc.logger
import jenm.yagoc.board.Board
import jenm.yagoc.board.BoardRules.isCorrectMove
import jenm.yagoc.board.BoardRules.moveDoesNotCreateCheck
import jenm.yagoc.board.BoardRules.noMoreMovesAllowed
import jenm.yagoc.board.Move
import jenm.yagoc.board.Square
import jenm.yagoc.pieces.*
import jenm.yagoc.players.ComputerPlayer
import jenm.yagoc.players.PlayerStrategy
import jenm.yagoc.players.UserPlayer
import jenm.yagoc.ui.UserOptionDialog
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

class Controller(private val board: Board, private val userOptions: UserOptionDialog) {
    private val checkpoints = ArrayList<Board>()
    private var finished = false
    private var paused = false

    fun resetBoard(board: Board) {
        checkpoints.clear()
        this.board.resetWith(board)
        nextMove()
    }

    fun undo() {
        if (checkpoints.isNotEmpty()) {
            board.resetWith(checkpoints.removeAt(checkpoints.size - 1))
            finished = false
            SwingUtilities.invokeLater { nextMove() }
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
        SwingUtilities.invokeLater { nextMove() }
    }

    fun moveIfPossible(from: Square, to: Square) =
        when {
            finished || !board.isPieceOfCurrentPlayer(board.pieceAt(from)) -> false
            board.currentPlayer().isUser -> {
                Move(board.pieceAt(from), from, to).let { move ->
                    if (from != to && isCorrectMove(board, move) && moveDoesNotCreateCheck(board, move)) {
                        board.play(move)
                        ifPawnHasReachedFinalRankReplaceWithQueen(board, move)
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

    fun ifPawnHasReachedFinalRankReplaceWithQueen(board: Board, move: Move) {
        //TODO What if there is already a queen?
        if (move.fromPiece == blackPawn && move.to.rank == 7) {
            board.pieceAt(move.to, blackQueen)
        } else if (move.fromPiece == whitePawn && move.to.rank == 0) {
            board.pieceAt(move.to, whiteQueen)
        }
    }

    fun nextMove() {
        if (finished || paused) return
        else if (board.currentPlayer().isComputer) {
            val checkpoint = Board(board)
            val move = board.currentPlayer().move(board)
            board.play(move)
            logger.info(move.toString())
            ifPawnHasReachedFinalRankReplaceWithQueen(board, move)
            finished = noMoreMovesAllowed(board)
            checkpoints.add(checkpoint)
        }
        if (board.currentPlayer().isComputer) {
            breath()
            SwingUtilities.invokeLater { nextMove() }
        }
    }

    private fun breath() {
        TimeUnit.SECONDS.sleep(COMPUTER_PAUSE_SECONDS.toLong())
    }

    fun newBoard() {
        board.reset()
    }

    fun configurePlayers() {
        val game = userOptions.gameType()
        logger.info("Type $game chosen")
        when (game) {
            1 -> {
                board.blackPlayer(
                    ComputerPlayer(
                        "computer",
                        PieceColor.BlackSet,
                        userOptions.getLevel("computer", 1),
                        PlayerStrategy.F1
                    )
                )
                board.whitePlayer(UserPlayer("user", PieceColor.WhiteSet))
            }
            2 -> {
                board.blackPlayer(UserPlayer("user", PieceColor.BlackSet))
                board.whitePlayer(
                    ComputerPlayer(
                        "computer",
                        PieceColor.WhiteSet,
                        userOptions.getLevel("computer", 1),
                        PlayerStrategy.F1
                    )
                )
            }
            3 -> {
                board.blackPlayer(
                    ComputerPlayer(
                        "computer 1",
                        PieceColor.BlackSet,
                        userOptions.getLevel("computer 1", 1),
                        PlayerStrategy.F1
                    )
                )
                board.whitePlayer(
                    ComputerPlayer(
                        "computer 2",
                        PieceColor.WhiteSet,
                        userOptions.getLevel("computer 2", 1),
                        PlayerStrategy.F1
                    )
                )
            }
            else -> {
                board.blackPlayer(UserPlayer("user 1", PieceColor.BlackSet))
                board.whitePlayer(UserPlayer("user 2", PieceColor.WhiteSet))
            }
        }
        logger.info("name\ttype")
        logger.info(board.blackPlayer().toString())
        logger.info(board.whitePlayer().toString())

        if (board.currentPlayer().isComputer) {
            SwingUtilities.invokeLater { nextMove() }
        }
    }

    fun togglePause() {
        paused = !paused
        logger.info(if (paused) "Game paused" else "Game resumed")
        if (!paused) {
            SwingUtilities.invokeLater { nextMove() }
        }
    }

    companion object {
        private const val COMPUTER_PAUSE_SECONDS = 1
    }
}