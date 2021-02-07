package yagoc.board

import yagoc.Yagoc
import yagoc.pieces.PieceColor
import yagoc.pieces.PieceType
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.streams.toList

object BoardRules {
    @JvmStatic
    fun isCorrectMove(board: BoardView, from: Square, to: Square): Boolean {
        return isCorrectMove(board, Move(board.pieceAt(from), from, to))
    }

    @JvmStatic
    fun isCorrectMove(board: BoardView, move: Move): Boolean {
        return move.fromPiece().isCorrectMove(board, move)
    }

    @JvmStatic
    fun cannotMoveWithoutBeingCheck(board: BoardView): Boolean {
        return generateMoves(board).stream().noneMatch { move: Move -> moveDoesNotCreateCheck(board, move) }
    }

    @JvmStatic
    fun isInCheck(board: BoardView, color: PieceColor): Boolean {
        // Could not find $color king!
        val kingSquare = allSquares.first { square: Square ->
            board.pieceAt(square).pieceType == PieceType.King && board.pieceAt(square).color == color
        }

        return allSquares
            .any { from: Square ->
                board.someAt(from) && board.pieceAt(from).color != color && isCorrectMove(
                    board,
                    from,
                    kingSquare
                )
            }
    }

    fun isCurrentPlayerCheckmate(board: BoardView) =
        if (isInCheck(board, board.currentPlayer().pieceColor)) {
            cannotMoveWithoutBeingCheck(board)
        } else {
            false
        }

    fun isADraw(board: BoardView) =
        if (!isInCheck(board, board.currentPlayer().pieceColor) && cannotMoveWithoutBeingCheck(board)) {
            true
        } else {
            board.drawCounter() == 50
        }

    @JvmStatic
    fun moveDoesNotCreateCheck(board: BoardView, move: Move): Boolean {
        return !board.playAndUndo(move) { isInCheck(board, move.fromPiece().color) }
    }

    @JvmStatic
    fun moveDoesNotCreateCheck(board: BoardView, from: Square, to: Square): Boolean {
        return moveDoesNotCreateCheck(board, Move(board.pieceAt(from), from, to))
    }

    @JvmStatic
    fun noMoreMovesAllowed(board: BoardView): Boolean {
        return when {
            isCurrentPlayerCheckmate(board) -> true.also {
                Yagoc.logger.info("checkmate winner is " + board.oppositePlayer().name)
            }
            isADraw(board) -> true.also {
                Yagoc.logger.info("draw")
            }
            else -> false
        }
    }

    @JvmStatic
    fun generateMoves(board: BoardView, from: Square): Stream<Move> {
        return board.pieceAt(from).generateMoves(board, from)
    }

    @JvmStatic
    fun generateMoves(board: BoardView, from: Square, predicate: Predicate<Move>): Stream<Move> {
        return generateMoves(board, from).filter(predicate)
    }

    @JvmStatic
    fun generateMoves(board: BoardView): Collection<Move> {
        return allSquares.flatMap { from: Square ->
            if (board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
                generateMoves(board, from) { move: Move -> moveDoesNotCreateCheck(board, move) }.toList()
            } else {
                emptyList()
            }
        }
    }
}