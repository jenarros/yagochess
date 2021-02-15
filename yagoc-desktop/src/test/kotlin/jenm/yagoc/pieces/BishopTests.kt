package jenm.yagoc.pieces

import jenm.yagoc.BoardSpec
import jenm.yagoc.board.c6Square
import jenm.yagoc.board.d5Square
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test

class BishopTests {
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