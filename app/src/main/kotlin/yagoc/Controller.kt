package yagoc

import yagoc.Yagoc.logger
import yagoc.board.Board
import yagoc.board.BoardRules.isCorrectMove
import yagoc.board.BoardRules.moveDoesNotCreateCheck
import yagoc.board.BoardRules.noMoreMovesAllowed
import yagoc.board.Move
import yagoc.board.Square
import yagoc.pieces.*
import yagoc.players.ComputerPlayer
import yagoc.players.PlayerStrategy
import yagoc.players.UserPlayer
import yagoc.ui.UserOptionDialog
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

    fun moveIfPossible(from: Square, to: Square): Boolean {
        if (finished || !board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
            return false
        } else if (board.currentPlayer().isUser) {
            val move = Move(board.pieceAt(from), from, to)
            if (from != to && isCorrectMove(board, move) && moveDoesNotCreateCheck(board, move)
            ) {
                board.play(move)
                ifPawnHasReachedFinalRankReplaceWithQueen(board, move)
                finished = noMoreMovesAllowed(board)
                logger.info(move.toString())
                return true
            }
        }
        return false
    }

    fun ifPawnHasReachedFinalRankReplaceWithQueen(board: Board, move: Move) {
        //TODO What if there is already a queen?
        if (move.fromPiece() == blackPawn && move.to().rank == 7) {
            board.pieceAt(move.to(), blackQueen)
        } else if (move.fromPiece() == whitePawn && move.to().rank == 0) {
            board.pieceAt(move.to(), whiteQueen)
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
                        PieceColor.blackSet,
                        userOptions.getLevel("computer", 1),
                        PlayerStrategy.F1
                    )
                )
                board.whitePlayer(UserPlayer("user", PieceColor.whiteSet))
            }
            2 -> {
                board.blackPlayer(UserPlayer("user", PieceColor.blackSet))
                board.whitePlayer(
                    ComputerPlayer(
                        "computer",
                        PieceColor.whiteSet,
                        userOptions.getLevel("computer", 1),
                        PlayerStrategy.F1
                    )
                )
            }
            3 -> {
                board.blackPlayer(
                    ComputerPlayer(
                        "computer 1",
                        PieceColor.blackSet,
                        userOptions.getLevel("computer 1", 1),
                        PlayerStrategy.F1
                    )
                )
                board.whitePlayer(
                    ComputerPlayer(
                        "computer 2",
                        PieceColor.whiteSet,
                        userOptions.getLevel("computer 2", 1),
                        PlayerStrategy.F1
                    )
                )
            }
            else -> {
                board.blackPlayer(UserPlayer("user 1", PieceColor.blackSet))
                board.whitePlayer(UserPlayer("user 2", PieceColor.whiteSet))
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