package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import yagoc.BoardSpec
import yagoc.board.b5Square
import yagoc.board.c5Square
import yagoc.board.c6Square
import yagoc.board.c7Square

class PawnTest {
    @Test
    fun `can move 1 square or two squares straigh ahead if it is in the starting rank`() {
        val test = BoardSpec(
            """
            --------
            --P-----
            --1-----
            --1-----
            --------
            --------
            --------
            --------
        """, c7Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `can move 1 square straigh ahead if it is not in the starting rank`() {
        val test = BoardSpec(
            """
            --------
            --------
            --P-----
            --1-----
            --------
            --------
            --------
            --------
        """, c6Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(1))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `can move 1 square diagonally if it can capture piece`() {
        val test = BoardSpec(
            """
            --------
            --------
            --P-----
            -pBp----
            --------
            --------
            --------
            --------
        """, c6Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `en passant`() {
        val test = BoardSpec(
            """
            --------
            --------
            -11-----
            -p------
            --------
            --------
            --------
            --------
        """, b5Square
        ).plusMove(blackPawn, c7Square, c5Square)

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}