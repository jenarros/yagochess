package jenm.yagoc.players

import jenm.yagoc.board.BoardRules.generateMoves
import jenm.yagoc.board.BoardView
import jenm.yagoc.board.Square
import jenm.yagoc.board.allSquares
import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.pieces.PieceType
import java.io.Serializable
import kotlin.math.abs

class DefaultPlayerStrategy : Serializable {
    operator fun invoke(board: BoardView, pieceColor: PieceColor) =
        allSquares.map { square: Square ->
            var acc = 0
            if (isPieceOurs(board, pieceColor, square)) {
                val piece = board.pieceAt(square)
                when (piece.pieceType) {
                    PieceType.Pawn -> {
                        acc += 100
                        acc += if (piece.color === PieceColor.BlackSet) square.rank * 20 else (7 - square.rank) * 20

                        // covered pawn is better
                        if (square.nextRank(pieceColor)
                                .exists() && square.file - 1 > 0 && square.file + 1 < 8 && (board.pieceAt(
                                square.nextRankPreviousFile(pieceColor)
                            ) == piece || board.pieceAt(square.nextRankPreviousFile(pieceColor)) == piece)
                        ) acc += 30
                    }
                    PieceType.Knight -> {
                        acc += (300 + (3.5 - abs(3.5 - square.file)) * 20).toInt()
                        acc += if (piece.color === PieceColor.BlackSet) (abs(3.5 - square.rank) * 10).toInt() else (abs(
                            3.5 - square.rank
                        ) * 10).toInt()
                    }
                    PieceType.Bishop -> acc += (300 + generateMoves(board, square).count() * 10).toInt()
                    PieceType.Rook -> acc += 500
                    PieceType.Queen -> {
                        acc += (940 + (3.5 - abs(3.5 - square.file)) * 20).toInt()
                        acc += if (piece.color === PieceColor.BlackSet) (abs(3.5 - square.rank) * 10).toInt() else (abs(
                            3.5 - square.rank
                        ) * 10).toInt()
                    }
                    else -> {
                    }
                }
            } else if (isPieceTheirs(board, pieceColor, square)) {
                val piece = board.pieceAt(square)
                when (piece.pieceType) {
                    PieceType.Pawn -> {
                        acc -= 100
                        acc -= if (piece.color === PieceColor.BlackSet) square.rank * 30 else (7 - square.rank) * 30
                        if (square.nextRank(pieceColor)
                                .exists() && square.file - 1 > 0 && square.file + 1 < 8 && (board.pieceAt(
                                square.nextRankPreviousFile(pieceColor)
                            ) == piece || board.pieceAt(square.nextRankPreviousFile(pieceColor)) == piece)
                        ) acc -= 20
                    }
                    PieceType.Knight -> acc -= (300 + (3.5 - abs(3.5 - square.file)) * 20).toInt()
                    PieceType.Bishop -> acc -= (330 + generateMoves(board, square).count() * 10).toInt()
                    PieceType.Rook -> acc -= 500
                    PieceType.Queen -> {
                        acc -= 1000
                        acc -= (abs(3.5 - square.rank) * 10).toInt()
                    }
                    else -> {
                    }
                }
            }
            acc
        }.sum()
//    companion object {
//        val F2 = PlayerStrategy { board: BoardView, set: PieceColor ->
//            allSquares.map { square: Square ->
//                var acc = 0
//                if (isPieceOurs(board, set, square)) {
//                    val piece = board.pieceAt(square)
//                    when (piece.pieceType) {
//                        PieceType.Pawn -> acc += 100
//                        PieceType.Knight -> acc += 300
//                        PieceType.Bishop -> acc += 330
//                        PieceType.Rook -> acc += 500
//                        PieceType.Queen -> acc += 940
//                        else -> {
//                        }
//                    }
//                } else if (isPieceTheirs(board, set, square)) {
//                    val piece = board.pieceAt(square)
//                    when (piece.pieceType) {
//                        PieceType.Pawn -> acc -= 100
//                        PieceType.Knight -> acc -= 300
//                        PieceType.Bishop -> acc -= 330
//                        PieceType.Rook -> acc -= 500
//                        PieceType.King -> acc -= 940
//                        else -> {
//                        }
//                    }
//                }
//                acc
//            }.sum()
//        }

    fun isPieceOurs(board: BoardView, pieceColor: PieceColor, square: Square) =
        board.pieceAt(square).color === pieceColor

    fun isPieceTheirs(board: BoardView, color: PieceColor, square: Square) =
        board.pieceAt(square).color !== color && !board.noneAt(square)
}