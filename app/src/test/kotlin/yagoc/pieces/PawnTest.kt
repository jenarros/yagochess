package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import yagoc.TestBoard
import yagoc.board.Square

class PawnTest {
    @Test
    fun `can move 1 square or two squares straigh ahead if it is in the starting rank`() {
        val test = TestBoard(
            """
            --------
            --P-----
            --1-----
            --1-----
            --------
            --------
            --------
            --------
        """, Square(1, 2)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `can move 1 square straigh ahead if it is not in the starting rank`() {
        val test = TestBoard(
            """
            --------
            --------
            --P-----
            --1-----
            --------
            --------
            --------
            --------
        """, Square(2, 2)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(1))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `can move 1 square diagonally if it can capture piece`() {
        val test = TestBoard(
            """
            --------
            --------
            --P-----
            -pBp----
            --------
            --------
            --------
            --------
        """, Square(2, 2)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares()))
        }
    }
}