package yagoc.board

import yagoc.pieces.Piece
import yagoc.pieces.Pieces.*
import java.io.Serializable
import java.util.*

class SquareBoard(val pieces: Array<Piece> = Array(64) { none }) : Serializable {
    operator fun get(square: Squares) = pieces[square.ordinal]

    operator fun get(squareIndex: Int) = pieces[squareIndex]

    operator fun set(square: Squares, piece: Piece) {
        pieces[square.ordinal] = piece
    }

    operator fun set(squareIndex: Int, piece: Piece) {
        pieces[squareIndex] = piece
    }

    fun reset() {
        Arrays.fill(pieces, none)
        set(Squares.a8, blackRook)
        set(Squares.b8, blackKnight)
        set(Squares.c8, blackBishop)
        set(Squares.d8, blackQueen)
        set(Squares.e8, blackKing)
        set(Squares.f8, blackBishop)
        set(Squares.g8, blackKnight)
        set(Squares.h8, blackRook)

        Squares.subset(Squares.a7, Squares.h7).forEach { set(it, blackPawn) }

        set(Squares.a1, whiteRook)
        set(Squares.b1, whiteKnight)
        set(Squares.c1, whiteBishop)
        set(Squares.d1, whiteQueen)
        set(Squares.e1, whiteKing)
        set(Squares.f1, whiteBishop)
        set(Squares.g1, whiteKnight)
        set(Squares.h1, whiteRook)

        Squares.subset(Squares.a2, Squares.h2).forEach { set(it, whitePawn) }
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