package yagoc

import yagoc.board.Board
import yagoc.board.Move
import yagoc.board.Square
import yagoc.pieces.Piece
import yagoc.pieces.Pieces
import kotlin.streams.toList

class BoardSpec(stringBoard: String, val fromSquare: Square) {
    val board = stringBoard.toBoard()

    /**
     * Valid squares for the current player are specified with value '1' or with a piece of the opposite set.
     */
    val validSquares = stringBoard.trimIndent().replace("\n", "").let { boardAsString ->
        Square.allSquares.filter {
            '1' == (boardAsString[it.arrayPosition()]) || Pieces.parse(boardAsString[it.arrayPosition()])
                .notOfSameColor(fromPiece().color())
        }.sorted().toList()
    }

    fun fromPiece(): Piece = board.pieceAt(fromSquare)

    fun possibleMoves() = fromPiece().generateMoves(board, fromSquare).map { it.to() }.sorted().toList()

    override fun toString() =
        "\n${board.toPrettyString()}\nFrom: $fromSquare"

    fun plusMove(piece: Piece, fromSquare: Square, toSquare: Square): BoardSpec {
        board.pieceAt(fromSquare, piece)
        board.play(Move(board.pieceAt(fromSquare), fromSquare, toSquare))
        return this
    }

    companion object {
        fun String.toBoard() = Board.parseBoard(this)
    }
}