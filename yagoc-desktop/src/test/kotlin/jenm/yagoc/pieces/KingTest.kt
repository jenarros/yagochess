package jenm.yagoc.pieces

import jenm.yagoc.BoardSpec
import jenm.yagoc.board.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class KingTest {
    @Test
    fun `can move 1 square in all directions`() {
        val test = BoardSpec(
            """
            --------
            --------
            --111---
            --1K1---
            --111---
            --------
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
    fun `can move 1 square in all directions if there isn't a piece of our color`() {
        val test = BoardSpec(
            """
            --------
            --------
            --111---
            --1Kp---
            --11P---
            --------
            --------
            --------
        """, d5Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `black set castling`() {
        val test = BoardSpec(
            """
            R-11K11R
            ---111--
            --------
            --------
            --------
            --------
            --------
            --------
        """, e8Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `cannot do castling if there is a piece in between`() {
        val test = BoardSpec(
            """
            R--QKB-R
            ---PPP--
            --------
            --------
            --------
            --------
            --------
            --------
        """, e8Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(0))
        }
    }

    @Test
    fun `black set castling is not allowed if pieces have been moved`() {
        val test = BoardSpec(
            """
            R--1K1-R
            ---PPP--
            --------
            --------
            --------
            --------
            --------
            --------
        """, e8Square
        )

        assertThat(test.plusMove(blackKing, d8Square, e8Square).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(blackRook, b8Square, a8Square).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(blackRook, g8Square, h8Square).possibleMoves().size, equalTo(2))
    }

    @Test
    fun `white set castling`() {
        val test = BoardSpec(
            """
            --------
            --------
            --------
            --------
            --------
            --------
            ---111--
            r-11k11r
        """, e1Square
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(7))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `white set castling is not allowed if pieces have been moved`() {
        val test = BoardSpec(
            """
            --------
            --------
            --------
            --------
            --------
            --------
            ---ppp--
            r--1k1-r
        """, e1Square
        )

        assertThat(test.plusMove(whiteKing, d1Square, e1Square).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(whiteRook, b1Square, a1Square).possibleMoves().size, equalTo(2))
        assertThat(test.plusMove(whiteRook, g1Square, h1Square).possibleMoves().size, equalTo(2))
    }
}