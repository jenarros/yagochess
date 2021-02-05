package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.Test
import yagoc.TestBoard
import yagoc.board.Square

class BishopTest {
    @Test
    fun `can move diagonally to the end of the board`() {
        val test = TestBoard(
            """
            1-----1-
            -1---1--
            --1-1---
            ---B----
            --1-1---
            -1---1--
            1-----1-
            -------1
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(13))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }

    @Test
    fun `can move diagonally to the end of the board or first piece found`() {
        val test = TestBoard(
            """
            --------
            --------
            --b-p---
            ---B----
            --1-1---
            -1---P--
            1-------
            --------
        """, Square(3, 3)
        )

        assertThat(test.possibleMoves().size, equalTo(6))
        assertThat(test.possibleMoves(), hasItem(equalTo(Square(2, 2))))
        assertThat(test.possibleMoves(), equalTo(test.validSquares()))
    }
}