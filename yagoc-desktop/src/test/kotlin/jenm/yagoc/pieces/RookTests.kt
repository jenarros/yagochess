package jenm.yagoc.pieces

import jenm.yagoc.BoardSpec
import jenm.yagoc.board.d2Square
import jenm.yagoc.board.d5Square
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test

class RookTests {

    @Test
    fun `can move in straight lines to the end of the board`() {
        val test = BoardSpec(
            """
            ---1----
            ---1----
            ---1----
            111R1111
            ---1----
            ---1----
            ---1----
            ---1----
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(14))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `can move in straight lines to the end of the board or first piece found`() {
        val test = BoardSpec(
            """
            ---1----
            ---1----
            ---1----
            P11R1111
            ---1----
            ---1----
            ---p----
            --------
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(12))
            assertThat(this, hasItem(equalTo(d2Square)))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}