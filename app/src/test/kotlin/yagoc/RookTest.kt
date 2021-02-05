package yagoc

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Test
import yagoc.pieces.PieceColor.blackSet
import yagoc.pieces.Pieces.blackRook
import kotlin.streams.toList

class RookTest {
    @Test
    fun `can move in straight lines to the end of the board`() {
        val test = """
            ---1----
            ---1----
            ---1----
            111R1111
            ---1----
            ---1----
            ---1----
            ---1----
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares(blackSet)

        val actual = blackRook.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()

        assertThat(actual.size, equalTo(14))
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `can move diagonally to the end of the board or first piece found`() {
        val test = """
            ---1----
            ---1----
            ---1----
            P11R1111
            ---1----
            ---1----
            ---p----
            --------
        """.trimIndent().replace("\n", "")

        val board = test.toBoard()
        val expected = test.toValidSquares(blackSet)

        val actual = blackRook.generateMoves(board, Square(3, 3)).map { it.to() }.sorted().toList()

        assertThat(actual.size, equalTo(12))
        assertThat(actual, hasItem(equalTo(Square(6, 3))))
        assertThat(actual, equalTo(expected))
    }
}