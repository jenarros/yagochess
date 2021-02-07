package yagoc.board

import yagoc.Yagoc
import yagoc.pieces.PieceColor
import yagoc.pieces.PieceType
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream

object BoardRules {
    fun isCorrectMove(board: BoardView, from: Square, to: Square): Boolean {
        return isCorrectMove(board, Move(board.pieceAt(from), from, to))
    }

    @JvmStatic
    fun isCorrectMove(board: BoardView, move: Move): Boolean {
        return move.fromPiece().isCorrectMove(board, move)
    }

    fun cannotMoveWithoutBeingCheck(board: BoardView): Boolean {
        return generateMoves(board).stream().noneMatch { move: Move -> moveDoesNotCreateCheck(board, move) }
    }

    @JvmStatic
    fun isInCheck(board: BoardView, color: PieceColor): Boolean {
        val kingSquare = Square.allSquares.stream()
            .filter { square: Square ->
                board.pieceAt(square).pieceType() == PieceType.King && board.pieceAt(square).color() == color
            }
            .findAny().orElseThrow { RuntimeException("Could not find $color king!") }
        return Square.allSquares.stream()
            .anyMatch { from: Square ->
                board.someAt(from) && board.pieceAt(from).color() != color && isCorrectMove(
                    board,
                    from,
                    kingSquare
                )
            }
    }

    fun isCheckmate(board: BoardView): Boolean {
        return if (isInCheck(board, board.currentPlayer().pieceColor())) {
            cannotMoveWithoutBeingCheck(board)
        } else {
            false
        }
    }

    fun isADraw(board: BoardView): Boolean {
        return if (!isInCheck(board, board.currentPlayer().pieceColor()) && cannotMoveWithoutBeingCheck(
                board
            )
        ) {
            true
        } else board.drawCounter() == 50
    }

    @JvmStatic
    fun moveDoesNotCreateCheck(board: BoardView, move: Move): Boolean {
        return !board.playAndUndo(move) { isInCheck(board, move.fromPiece().color()) }
    }

    @JvmStatic
    fun moveDoesNotCreateCheck(board: BoardView, from: Square, to: Square): Boolean {
        return moveDoesNotCreateCheck(board, Move(board.pieceAt(from), from, to))
    }

    @JvmStatic
    fun noMoreMovesAllowed(board: BoardView): Boolean {
        return when {
            isCheckmate(board) -> {
                Yagoc.logger.info("checkmate winner is " + board.oppositePlayer().name())
                true
            }
            isADraw(board) -> {
                Yagoc.logger.info("draw")
                true
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
        return Square.allSquares.stream().flatMap { from: Square ->
            if (board.isPieceOfCurrentPlayer(board.pieceAt(from))) {
                generateMoves(board, from) { move: Move -> moveDoesNotCreateCheck(board, move) }
            } else {
                Stream.empty()
            }
        }.collect(Collectors.toList())
    }
}