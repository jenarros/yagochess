package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import yagoc.TestBoard
import yagoc.board.Square
import yagoc.pieces.Pieces.*

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

        test.possibleMoves().run {
            assertThat(this.size, equalTo(8))
            assertThat(this, equalTo(test.validSquares()))
        }
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

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `black set castling`() {
        val test = TestBoard(
            """
            R-11K11R
            ---111--
            --------
            --------
            --------
            --------
            --------
            --------
        """, Square(0, 4)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `black set castling is not allowed if pieces have been moved`() {
        val test = TestBoard(
            """
            R--1K1-R
            ---PPP--
            --------
            --------
            --------
            --------
            --------
            --------
        """, Square(0, 4)
        )

        assertThat(test.plusMove(blackKing, Square(0, 3), Square(0, 4)).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(blackRook, Square(0, 1), Square(0, 0)).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(blackRook, Square(0, 6), Square(0, 7)).possibleMoves().size, equalTo(2))
    }

    @Test
    fun `white set castling`() {
        val test = TestBoard(
            """
            --------
            --------
            --------
            --------
            --------
            --------
            ---111--
            r-11k11r
        """, Square(7, 4)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares()))
        }
    }

    @Test
    fun `white set castling is not allowed if pieces have been moved`() {
        val test = TestBoard(
            """
            --------
            --------
            --------
            --------
            --------
            --------
            ---ppp--
            r--1k1-r
        """, Square(7, 4)
        )

        assertThat(test.plusMove(whiteKing, Square(7, 3), Square(7, 4)).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(whiteRook, Square(7, 1), Square(7, 0)).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(whiteRook, Square(7, 6), Square(7, 7)).possibleMoves().size, equalTo(2))
    }
}