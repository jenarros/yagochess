package yagoc

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasItem
import org.junit.Test
import yagoc.pieces.PieceColor.blackSet
import yagoc.pieces.Pieces.blackBishop
import kotlin.streams.toList

class BishopTest {
    @Test
    fun `can move diagonally to the end of the board`() {
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
        val expected = test.toValidSquares(blackSet)

        val actual = blackBishop.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()

        assertThat(actual.size, Matchers.equalTo(13))
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `can move diagonally to the end of the board or first piece found`() {
        val test = """
            --------
            --------
            --b-p---
            ---B----
            --1-1---
            -1---P--
            1-------
            --------
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares(blackSet)

        val actual = blackBishop.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()

        assertThat(actual.size, Matchers.equalTo(6))
        assertThat(actual, hasItem(equalTo(Square(2, 2))))
        assertThat(actual, equalTo(expected))
    }
}