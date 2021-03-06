package jenm.yagoc.pieces

import jenm.yagoc.BoardSpec
import jenm.yagoc.board.d5Square
import jenm.yagoc.board.f4Square
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test

class KnightTests {
    @Test
    fun `standard 8 squares`() {
        val test = BoardSpec(
            """
            --------
            --1-1---
            -1---1--
            ---N----
            -1---1--
            --1-1---
            --------
            --------
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(8))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `standard 8 squares if they are empty or have a piece of the other color`() {
        val test = BoardSpec(
            """
            --------
            --1-1---
            -1---1--
            ---N----
            -Q---p--
            --1-1---
            --------
            --------
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, hasItem(equalTo(f4Square)))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}