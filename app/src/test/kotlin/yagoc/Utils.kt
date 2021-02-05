package yagoc

import yagoc.pieces.PieceColor
import yagoc.pieces.Pieces

/**
 * Valid squares for the current player are specified with value '1' or with a piece of the opposite set.
 */
fun String.toValidSquares(currentSet: PieceColor): Collection<Square> =
    Square.allSquares.filter {
        '1' == (this[it.arrayPosition()]) || Pieces.parse(this[it.arrayPosition()]).notOfSameColor(currentSet)
    }.sorted().toList()

/**
 * Converts a String to a board, lowercase pieces belong to the white set and uppercase pieces to the black set.
 */
fun String.toBoard(): Board {
    val board = Board()
    Square.allSquares.forEach {
        board.pieceAt(it, Pieces.parse(this[it.arrayPosition()]))
    }
    return board
}