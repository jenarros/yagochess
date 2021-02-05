package yagoc

import yagoc.board.Board
import yagoc.board.Move
import yagoc.board.Square
import yagoc.pieces.Piece
import yagoc.pieces.Pieces
import kotlin.streams.toList

class TestBoard(private val stringBoard: String, val fromSquare: Square) {
    private val stringTestBoard = stringBoard.trimIndent().replace("\n", "")

    val board = Board().also { board ->
        Square.allSquares.forEach {
            board.pieceAt(it, Pieces.parse(stringTestBoard[it.arrayPosition()]))
        }
    }

    /**
     * Valid squares for the current player are specified with value '1' or with a piece of the opposite set.
     */
    fun validSquares(): Collection<Square> =
        Square.allSquares.filter {
            '1' == (stringTestBoard[it.arrayPosition()]) || Pieces.parse(stringTestBoard[it.arrayPosition()])
                .notOfSameColor(fromPiece().color())
        }.sorted().toList()

    fun fromPiece(): Piece = board.pieceAt(fromSquare)

    fun possibleMoves() = fromPiece().generateMoves(board, fromSquare).map { it.to() }.sorted().toList()

    override fun toString() =
        "\n${stringBoard.trimIndent()}\nFrom: $fromSquare"

    fun plusMove(piece: Piece, fromSquare: Square, toSquare: Square): TestBoard {
        board.pieceAt(fromSquare, piece)
        board.play(Move(board.pieceAt(fromSquare), fromSquare, toSquare))
        return this
    }
}