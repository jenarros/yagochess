package jenm.yagoc.board

import jenm.yagoc.pieces.*
import java.io.Serializable
import java.util.*

class SquareBoard(val pieces: Array<Piece> = Array(64) { none }) : Serializable {
    operator fun get(squareIndex: Int) = pieces[squareIndex]

    operator fun set(squareIndex: Int, piece: Piece) {
        pieces[squareIndex] = piece
    }

    fun reset() {
        Arrays.fill(pieces, none)
        pieces[a8] = blackRook
        pieces[b8] = blackKnight
        pieces[c8] = blackBishop
        pieces[d8] = blackQueen
        pieces[e8] = blackKing
        pieces[f8] = blackBishop
        pieces[g8] = blackKnight
        pieces[h8] = blackRook

        allSquares.copyOfRange(a7, h7 + 1).forEach { pieces[it.arrayPosition()] = blackPawn }

        pieces[a1] = whiteRook
        pieces[b1] = whiteKnight
        pieces[c1] = whiteBishop
        pieces[d1] = whiteQueen
        pieces[e1] = whiteKing
        pieces[f1] = whiteBishop
        pieces[g1] = whiteKnight
        pieces[h1] = whiteRook

        allSquares.copyOfRange(a2, h2 + 1).forEach { set(it.arrayPosition(), whitePawn) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SquareBoard

        if (!pieces.contentEquals(other.pieces)) return false

        return true
    }

    override fun hashCode() = pieces.contentHashCode()
}