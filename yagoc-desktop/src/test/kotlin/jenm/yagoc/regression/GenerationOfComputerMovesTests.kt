package jenm.yagoc.regression

import jenm.yagoc.BoardSpec.Companion.toBoard
import jenm.yagoc.board.c2Square
import jenm.yagoc.board.c3Square
import jenm.yagoc.board.d5Square
import jenm.yagoc.defaultLevel
import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.players.MinimaxPlayer
import jenm.yagoc.players.NodeBoardValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

class GenerationOfComputerMovesTests {

    @Test
    @Timeout(3000, unit = TimeUnit.MILLISECONDS) // Before migration to Kotlin, it was always under 4s
    fun `can generate moves fast`() {
        val minimaxPlayer = MinimaxPlayer(PieceColor.WhiteSet, defaultLevel)

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

        minimaxPlayer.alphaBeta(defaultLevel, board, Int.MIN_VALUE, Int.MAX_VALUE)

        assertThat(minimaxPlayer.processedMoveCounter.get(), equalTo(272896))
    }

    @Test
    @Timeout(500, unit = TimeUnit.MILLISECONDS)
    fun `minimax - pawn to d5 is second move`() {
        val minimaxPlayer = MinimaxPlayer(PieceColor.BlackSet, 3)

        val board = """
            RNBQKBNR
            PPPPPPPP
            --------
            --------
            --------
            --------
            pppppppp
            rnbqkbnr
        """.toBoard().play(c2Square, c3Square)

        board.blackPlayer(minimaxPlayer)

        val boardValue = minimaxPlayer.move(board)

        assertThat((boardValue as NodeBoardValue).move.to, equalTo(d5Square))
    }
}
