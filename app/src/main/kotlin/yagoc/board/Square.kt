package yagoc.board

import yagoc.pieces.PieceColor
import java.io.Serializable
import java.util.*
import java.util.stream.Collectors

data class Square(val rank: Int, val file: Int) : Serializable, Comparable<Square> {
    constructor(arrayPosition: Int) : this(arrayPosition / 8, arrayPosition % 8)

    fun rank() = rank

    fun file() = file

    fun nextRank(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank - 1, file)
        } else {
            Square(rank + 1, file)
        }
    }

    fun next2Rank(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank - 2, file)
        } else {
            Square(rank + 2, file)
        }
    }

    fun next2File(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank, file + 2)
        } else {
            Square(rank, file - 2)
        }
    }

    fun previous2File(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank, file - 2)
        } else {
            Square(rank, file + 2)
        }
    }

    fun nextRankNextFile(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank - 1, file + 1)
        } else {
            Square(rank + 1, file - 1)
        }
    }

    fun nextRankPreviousFile(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank - 1, file - 1)
        } else {
            Square(rank + 1, file + 1)
        }
    }

    fun previousFile(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank, file - 1)
        } else {
            Square(rank, file + 1)
        }
    }

    fun nextFile(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank, file + 1)
        } else {
            Square(rank, file - 1)
        }
    }

    fun previousRank(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank + 1, file)
        } else {
            Square(rank - 1, file)
        }
    }

    fun previous2Rank(pieceColor: PieceColor): Square {
        return if (pieceColor == PieceColor.whiteSet) {
            Square(rank + 2, file)
        } else {
            Square(rank - 2, file)
        }
    }

    fun exists(): Boolean {
        return rank in 0..7 && file in 0..7
    }

    fun diagonalSquares(): List<Square> {
        val acc: MutableList<Square> = ArrayList()
        for (distance in 1..7) {
            acc.add(Square(rank + distance, file + distance))
            acc.add(Square(rank + distance, file - distance))
            acc.add(Square(rank - distance, file + distance))
            acc.add(Square(rank - distance, file - distance))
        }
        return acc.stream().filter { obj: Square -> obj.exists() }.collect(Collectors.toList())
    }

    fun straightSquares(): List<Square> {
        val acc: MutableList<Square> = ArrayList()
        for (distance in 1..7) {
            acc.add(Square(rank + distance, file))
            acc.add(Square(rank - distance, file))
            acc.add(Square(rank, file + distance))
            acc.add(Square(rank, file - distance))
        }
        return acc.stream().filter { obj: Square -> obj.exists() }.collect(Collectors.toList())
    }

    override fun toString(): String {
        return "(" + Move.RANK_NAMES[rank] + ", " + Move.FILE_NAMES[file] + ')'
    }

    /**
     * position of the square in a sequence from 0 to 63
     */
    fun arrayPosition(): Int {
        return rank * 8 + file
    }

    override fun compareTo(other: Square): Int {
        return arrayPosition().compareTo(other.arrayPosition())
    }

    companion object {
        @JvmField
        var allSquares = all()
        private fun all(): List<Square> {
            val squares: MutableList<Square> = ArrayList()
            for (rank in 0..7) {
                for (file in 0..7) {
                    squares.add(Square(rank, file))
                }
            }
            return squares
        }
    }
}