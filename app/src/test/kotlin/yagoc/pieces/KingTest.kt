package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import yagoc.TestBoard
import yagoc.board.Square

class KingTest {
    @Test
    fun `can move 1 square in all directions`() {
        val test = TestBoard(
            """
            --------
            --------
            --111---
            --1K1---
            --111---
            --------
            --------
            --------
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(8))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }

    @Test
    fun `can move 1 square in all directions if there isn't a piece of our color`() {
        val test = TestBoard(
            """
            --------
            --------
            --111---
            --1Kp---
            --11P---
            --------
            --------
            --------
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(7))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }
}