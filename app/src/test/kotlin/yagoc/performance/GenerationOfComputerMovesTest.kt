package yagoc.performance

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import yagoc.BoardSpec.Companion.toBoard
import yagoc.pieces.PieceColor
import yagoc.players.ComputerPlayer
import yagoc.players.PlayerStrategy
import java.util.concurrent.atomic.AtomicInteger

class GenerationOfComputerMovesTest {

    private val counter = AtomicInteger()

    @Test
    @Timeout(7) // Before using the Scoreboard class, it was a bit faster and always under 4s
    fun `can generate moves fast`() {
        val level3 = ComputerPlayer("level3", PieceColor.whiteSet, 4, PlayerStrategy.F1)

        val board = """
            R---K-NR
            PPPN-P-P
            --------
            ------b-
            p--pB-p-
            -pB-----
            ---nb--p
            --r-k-n-
        """.toBoard()

        level3.alphaBeta(4, board, Int.MIN_VALUE, Int.MAX_VALUE, counter)

        assertThat(counter.get(), equalTo(273007))
    }
}