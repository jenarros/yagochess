package jenm.yagoc.board

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class SquareTest {

    @Test
    fun `can calculate arrayPosition of square`() {
        assertThat(square(0, 0).arrayPosition(), equalTo(0))
        assertThat(square(0, 1).arrayPosition(), equalTo(1))
        assertThat(square(2, 2).arrayPosition(), equalTo(18))
        assertThat(square(2, 3).arrayPosition(), equalTo(19))
        assertThat(square(7, 7).arrayPosition(), equalTo(63))
    }

    @Test
    fun `can calculate rank and file from arrayPosition`() {
        assertThat(Square(0), equalTo(square(0, 0)))
        assertThat(Square(1), equalTo(square(0, 1)))
        assertThat(Square(18), equalTo(square(2, 2)))
        assertThat(Square(19), equalTo(square(2, 3)))
        assertThat(Square(63), equalTo(square(7, 7)))
    }
}