package yagoc.board

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import yagoc.BoardSpec.Companion.toBoard
import yagoc.board.BoardRules.generateMoves

class BoardTest {
    @Test
    fun `can parse and print board`() {
        val expected = """
            --------
            --------
            --------
            ---Q----
            --------
            --------
            -------q
            -------k
            """
        assertThat(expected.toBoard().toPrettyString(), equalTo(expected.trimIndent()))
    }

    @Test
    fun `can generate all possible moves for a known setup`() {
        val expected = """
            ----r---
            --q-----
            ------r-
            -k-b-n-p
            ---b----
            ---n----
            p------p
            -K------
        """.toBoard()

        assertThat(generateMoves(expected).size, equalTo(103))
    }

    @Test
    fun `can compare boards`() {
        val expected = """
            RNBQKBNR
            PPPPPPPP
            --------
            --------
            --------
            --------
            pppppppp
            rnbqkbnr
        """.toBoard()

        val actual = Board(expected)
        assertThat(actual, equalTo(expected))
    }

    @Test
    fun `can undo castling`() {
        val expected = """
            R---K--R
            PPPPPPPP
            --------
            --------
            --------
            --------
            pppppppp
            r---k--r
        """.toBoard()

        assertThat(Board(expected).playAndUndo(Square(7, 4), Square(7, 6)), equalTo(expected))
        assertThat(Board(expected).playAndUndo(Square(7, 4), Square(7, 2)), equalTo(expected))
        assertThat(Board(expected).playAndUndo(Square(0, 4), Square(0, 6)), equalTo(expected))
        assertThat(Board(expected).playAndUndo(Square(0, 4), Square(0, 2)), equalTo(expected))
    }
}