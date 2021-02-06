package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import yagoc.BoardSpec
import yagoc.board.Square
import yagoc.pieces.Pieces.blackPawn

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
        """, Square(1, 2)
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
        """, Square(2, 2)
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
        """, Square(2, 2)
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
        """, Square(3, 1)
        ).plusMove(blackPawn, Square(1, 2), Square(3, 2))

        test.possibleMoves().run {
            assertThat(this.size, equalTo(2))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}