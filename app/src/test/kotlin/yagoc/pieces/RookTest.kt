package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
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

        test.possibleMoves().run {
            assertThat(this.size, equalTo(14))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `can move in straight lines to the end of the board or first piece found`() {
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

        test.possibleMoves().run {
            assertThat(this.size, equalTo(12))
            assertThat(this, hasItem(equalTo(Square(6, 3))))
            assertThat(this, equalTo(test.validSquares()))
        }
    }
}