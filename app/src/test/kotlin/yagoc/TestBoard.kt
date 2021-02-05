package yagoc

import yagoc.board.Board
import yagoc.board.Square
import yagoc.pieces.Piece
import yagoc.pieces.Pieces
import kotlin.streams.toList

class TestBoard(private val stringBoard: String, val fromSquare: Square) {
    private val stringTestBoard = stringBoard.trimIndent().replace("\n", "")

    /**
     * Valid squares for the current player are specified with value '1' or with a piece of the opposite set.
     */
    fun validSquares(): Collection<Square> =
        Square.allSquares.filter {
            '1' == (stringTestBoard[it.arrayPosition()]) || Pieces.parse(stringTestBoard[it.arrayPosition()])
                .notOfSameColor(fromPiece().color())
        }.sorted().toList()

    /**
     * Converts a String to a board, lowercase pieces belong to the white set and uppercase pieces to the black set.
     */
    fun board(): Board {
        return Board().also { board ->
            Square.allSquares.forEach {
                board.pieceAt(it, Pieces.parse(stringTestBoard[it.arrayPosition()]))
            }
        }
    }

    fun fromPiece(): Piece = board().pieceAt(fromSquare)

    fun possibleMoves() = fromPiece().generateMoves(board(), fromSquare).map { it.to() }.sorted().toList()

    override fun toString() =
        "\n${stringBoard.trimIndent()}\nFrom: $fromSquare"
}