package yagoc.board

import yagoc.pieces.Piece
import yagoc.pieces.Pieces.*
import java.io.Serializable
import java.util.*

class SquareBoard(val pieces: Array<Piece> = Array(64) { none }) : Serializable {
    operator fun get(squareIndex: Int) = pieces[squareIndex]

    operator fun set(squareIndex: Int, piece: Piece) {
        pieces[squareIndex] = piece
    }

    fun reset() {
        Arrays.fill(pieces, none)
        set(a8, blackRook)
        set(b8, blackKnight)
        set(c8, blackBishop)
        set(d8, blackQueen)
        set(e8, blackKing)
        set(f8, blackBishop)
        set(g8, blackKnight)
        set(h8, blackRook)

        allSquares.copyOfRange(a7, h7 + 1).forEach { set(it.arrayPosition(), blackPawn) }

        set(a1, whiteRook)
        set(b1, whiteKnight)
        set(c1, whiteBishop)
        set(d1, whiteQueen)
        set(e1, whiteKing)
        set(f1, whiteBishop)
        set(g1, whiteKnight)
        set(h1, whiteRook)

        allSquares.copyOfRange(a2, h2 + 1).forEach { set(it.arrayPosition(), whitePawn) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SquareBoard

        if (!pieces.contentEquals(other.pieces)) return false

        return true
    }

    override fun hashCode(): Int {
        return pieces.contentHashCode()
    }
}