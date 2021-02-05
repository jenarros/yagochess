package yagoc.board

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class SquareTest {

    @Test
    fun `can calculate arrayPosition of square`() {
        assertThat(Square(0, 0).arrayPosition(), equalTo(0))
        assertThat(Square(0, 1).arrayPosition(), equalTo(8))
        assertThat(Square(7, 7).arrayPosition(), equalTo(63))
    }
}