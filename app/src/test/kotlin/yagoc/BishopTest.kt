package yagoc

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import yagoc.pieces.Pieces
import kotlin.streams.toList

class BishopTest {
    @Test
    fun `can move diagonally`() {
        val test = """
            1-----1-
            -1---1--
            --1-1---
            ---B----
            --1-1---
            -1---1--
            1-----1-
            -------1
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares()

        val actual = Pieces.whiteBishop.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()
        assertThat(actual, equalTo(expected))
    }

    private fun String.toValidSquares(): Collection<Square> =
            Square.allSquares.filter {
                '1' == (this[it.arrayPosition()])
            }.sorted().toList()

    private fun String.toBoard(): Board {
        val board = Board()
        Square.allSquares.forEach {
            board.pieceAt(it, Pieces.parse(this[it.arrayPosition()]))
        }
        return board
    }

    @Test
    fun `represent squares in array`() {
        assertThat(Square(0, 0).arrayPosition(), equalTo(0))
        assertThat(Square(0, 1).arrayPosition(), equalTo(8))
        assertThat(Square(7, 7).arrayPosition(), equalTo(63))
    }
}