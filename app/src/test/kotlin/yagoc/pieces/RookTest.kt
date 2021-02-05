package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Test
import yagoc.TestBoard
import yagoc.board.Square

class RookTest {

    @Test
    fun `can move in straight lines to the end of the board`() {
        val test = TestBoard(
            """
            ---1----
            ---1----
            ---1----
            111R1111
            ---1----
            ---1----
            ---1----
            ---1----
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(14))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }

    @Test
    fun `can move diagonally to the end of the board or first piece found`() {
        val test = TestBoard(
            """
            ---1----
            ---1----
            ---1----
            P11R1111
            ---1----
            ---1----
            ---p----
            --------
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(12))
        assertThat(test.possibleMoves(), hasItem(equalTo(Square(6, 3))))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }
}