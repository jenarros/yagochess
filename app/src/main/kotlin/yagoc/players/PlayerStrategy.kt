package yagoc.players

import yagoc.board.BoardRules.generateMoves
import yagoc.board.BoardView
import yagoc.board.Square
import yagoc.board.allSquares
import yagoc.pieces.PieceColor
import yagoc.pieces.PieceType
import java.io.Serializable
import kotlin.math.abs

class PlayerStrategy(private val strategy: (BoardView, PieceColor) -> Int) : Serializable {
    fun apply(board: BoardView, pieceColor: PieceColor): Int {
        return strategy(board, pieceColor)
    }

    companion object {
        @JvmField
        var F1 = PlayerStrategy { board: BoardView, color: PieceColor ->
            allSquares.map { square: Square ->
                var acc = 0
                if (isPieceOurs(board, color, square)) {
                    val piece = board.pieceAt(square)
                    when (piece.pieceType()) {
                        PieceType.Pawn -> {
                            acc += 100
                            acc += if (piece.color() === PieceColor.blackSet) square.rank() * 20 else (7 - square.rank()) * 20

                            // covered pawn is better
                            if (square.nextRank(color)
                                    .exists() && square.file() - 1 > 0 && square.file() + 1 < 8 && (board.pieceAt(
                                    square.nextRankPreviousFile(color)
                                ) == piece || board.pieceAt(square.nextRankPreviousFile(color)) == piece)
                            ) acc += 30
                        }
                        PieceType.Knight -> {
                            acc += (300 + (3.5 - abs(3.5 - square.file())) * 20).toInt()
                            acc += if (piece.color() === PieceColor.blackSet) (abs(3.5 - square.rank()) * 10).toInt() else (abs(
                                3.5 - square.rank()
                            ) * 10).toInt()
                        }
                        PieceType.Bishop -> acc += (300 + generateMoves(board, square).count() * 10).toInt()
                        PieceType.Rook -> acc += 500
                        PieceType.Queen -> {
                            acc += (940 + (3.5 - abs(3.5 - square.file())) * 20).toInt()
                            acc += if (piece.color() === PieceColor.blackSet) (abs(3.5 - square.rank()) * 10).toInt() else (abs(
                                3.5 - square.rank()
                            ) * 10).toInt()
                        }
                        else -> {
                        }
                    }
                } else if (isPieceTheirs(board, color, square)) {
                    val piece = board.pieceAt(square)
                    when (piece.pieceType()) {
                        PieceType.Pawn -> {
                            acc -= 100
                            acc -= if (piece.color() === PieceColor.blackSet) square.rank() * 30 else (7 - square.rank()) * 30
                            if (square.nextRank(color)
                                    .exists() && square.file() - 1 > 0 && square.file() + 1 < 8 && (board.pieceAt(
                                    square.nextRankPreviousFile(color)
                                ) == piece || board.pieceAt(square.nextRankPreviousFile(color)) == piece)
                            ) acc -= 20
                        }
                        PieceType.Knight -> acc -= (300 + (3.5 - abs(3.5 - square.file())) * 20).toInt()
                        PieceType.Bishop -> acc -= (330 + generateMoves(board, square).count() * 10).toInt()
                        PieceType.Rook -> acc -= 500
                        PieceType.Queen -> {
                            acc -= 1000
                            acc -= (abs(3.5 - square.rank()) * 10).toInt()
                        }
                        else -> {
                        }
                    }
                }
                acc
            }.sum()
        }

        @JvmField
        var F2 = PlayerStrategy { board: BoardView, set: PieceColor ->
            allSquares.map { square: Square ->
                var acc = 0
                if (isPieceOurs(board, set, square)) {
                    val piece = board.pieceAt(square)
                    when (piece.pieceType()) {
                        PieceType.Pawn -> acc += 100
                        PieceType.Knight -> acc += 300
                        PieceType.Bishop -> acc += 330
                        PieceType.Rook -> acc += 500
                        PieceType.Queen -> acc += 940
                        else -> {
                        }
                    }
                } else if (isPieceTheirs(board, set, square)) {
                    val piece = board.pieceAt(square)
                    when (piece.pieceType()) {
                        PieceType.Pawn -> acc -= 100
                        PieceType.Knight -> acc -= 300
                        PieceType.Bishop -> acc -= 330
                        PieceType.Rook -> acc -= 500
                        PieceType.King -> acc -= 940
                        else -> {
                        }
                    }
                }
                acc
            }.sum()
        }

        fun isPieceOurs(board: BoardView, pieceColor: PieceColor, square: Square): Boolean {
            return board.pieceAt(square).color() === pieceColor
        }

        fun isPieceTheirs(board: BoardView, color: PieceColor, square: Square): Boolean {
            return board.pieceAt(square).color() !== color && !board.noneAt(square)
        }
    }
}