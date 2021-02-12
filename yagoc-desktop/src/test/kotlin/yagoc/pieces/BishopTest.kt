package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
import yagoc.BoardSpec
import yagoc.board.c6Square
import yagoc.board.d5Square

class BishopTest {
    @Test
    fun `can move diagonally to the end of the board`() {
        val test = BoardSpec(
            """
            1-----1-
            -1---1--
            --1-1---
            ---B----
            --1-1---
            -1---1--
            1-----1-
            -------1
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(13))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `can move diagonally to the end of the board or first piece found`() {
        val test = BoardSpec(
            """
            --------
            --------
            --b-p---
            ---B----
            --1-1---
            -1---P--
            1-------
            --------
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(6))
            assertThat(this, hasItem(equalTo(c6Square)))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}