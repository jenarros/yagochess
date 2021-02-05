package yagoc

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import yagoc.pieces.PieceColor.blackSet
import yagoc.pieces.Pieces.blackKing
import kotlin.streams.toList

class KingTest {
    @Test
    fun `can move 1 square in all directions`() {
        val test = """
            --------
            --------
            --111---
            --1K1---
            --111---
            --------
            --------
            --------
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares(blackSet)

        val actual = blackKing.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()
        assertThat(actual.size, equalTo(8))
        assertThat(actual, CoreMatchers.equalTo(expected))
    }

    @Test
    fun `can move 1 square in all directions if there isn't a piece of our color`() {
        val test = """
            --------
            --------
            --111---
            --1Kp---
            --11P---
            --------
            --------
            --------
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares(blackSet)


        val actual = blackKing.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()
        assertThat(actual.size, equalTo(7))
        assertThat(actual, CoreMatchers.equalTo(expected))
    }
}