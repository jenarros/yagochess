package yagoc.board

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class SquaresTest {

    @Test
    fun `square enum value can be converted to legacy rank and file`() {
        assertThat(Squares.a8.legacySquare(), equalTo(Square(0, 0)))
        assertThat(Squares.a1.legacySquare(), equalTo(Square(7, 0)))
        assertThat(Squares.h1.legacySquare(), equalTo(Square(7, 7)))
    }

    @Test
    fun `can convert square enum value to array position`() {
        assertThat(Squares.a1.ordinal, equalTo(Squares.a1.legacySquare().arrayPosition()))
        assertThat(Squares.c6.ordinal, equalTo(Squares.c6.legacySquare().arrayPosition()))
        assertThat(Squares.valueOf(7, 0), equalTo(Squares.a1))
    }
}