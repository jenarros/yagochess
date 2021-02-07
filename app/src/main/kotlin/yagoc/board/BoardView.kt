package yagoc.board

import yagoc.pieces.Piece
import yagoc.pieces.none
import yagoc.players.Player
import java.io.Serializable
import java.util.concurrent.Callable

interface BoardView : Serializable {
    fun pieceAt(square: Square): Piece
    fun noneAt(square: Square): Boolean {
        return pieceAt(square) == none
    }

    fun someAt(square: Square): Boolean {
        return !noneAt(square)
    }

    fun enPassant(file: Int): Int
    fun currentPlayer(): Player
    fun oppositePlayer(): Player
    fun isPieceOfCurrentPlayer(piece: Piece): Boolean
    fun hasWhiteLeftRookMoved(): Boolean
    fun hasWhiteRightRookMoved(): Boolean
    fun hasWhiteKingMoved(): Boolean
    fun hasBlackLeftRookMoved(): Boolean
    fun hasBlackRightRookMoved(): Boolean
    fun hasBlackKingMoved(): Boolean
    fun drawCounter(): Int
    fun moveCounter(): Int
    fun <T> playAndUndo(move: Move, callable: Callable<T>): T
    fun playAndUndo(from: Square, to: Square): BoardView
    fun toPrettyString(): String
}