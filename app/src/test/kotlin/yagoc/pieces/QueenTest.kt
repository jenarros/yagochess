package yagoc.pieces

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
import yagoc.BoardSpec
import yagoc.board.Square

class QueenTest {

    @Test
    fun `like a rook and bishop to the end of the board`() {
        val test = BoardSpec(
            """
            1--1--1-
            -1-1-1--
            --111---
            111Q1111
            --111---
            -1-1-1--
            1--1--1-
            ---1---1
        """, Square(3, 3)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(27))
            assertThat(this, equalTo(test.validSquares))
        }
    }

    @Test
    fun `like a rook or bishop to the end of the board or first piece found`() {
        val test = BoardSpec(
            """
            1--1--1-
            -1-1-1--
            --111---
            P11Q1111
            --111---
            -1-1-1--
            1--p--1-
            -------1
        """, Square(3, 3)
        )

        test.possibleMoves().run {
            assertThat(this.size, equalTo(25))
            assertThat(this, hasItem(equalTo(Square(6, 3))))
            assertThat(this, equalTo(test.validSquares))
        }
    }
}