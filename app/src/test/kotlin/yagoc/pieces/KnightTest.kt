package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
import yagoc.BoardSpec
import yagoc.board.Square

class KnightTest {
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
        """, Square(3, 3)
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
        """, Square(3, 3)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, hasItem(equalTo(Square(4, 5))))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}