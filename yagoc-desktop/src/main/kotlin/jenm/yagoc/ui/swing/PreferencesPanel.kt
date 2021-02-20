package jenm.yagoc.ui.swing

import jenm.yagoc.YagocSettings
import jenm.yagoc.pieces.PieceColor
import jenm.yagoc.players.MinimaxPlayer
import jenm.yagoc.players.Player
import jenm.yagoc.players.PlayerStrategy
import jenm.yagoc.players.UserPlayer
import java.awt.GridLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*

class PreferencesPanel(
    parentWindow: JFrame,
    currentSettings: YagocSettings,
    saveCallback: (YagocSettings) -> Unit,
    cancelCallback: () -> Unit
) :
    JDialog(parentWindow, "Preferences") {
    private val computerPlayerLabel = "Computer"
    private val userPlayerLabel = "User"
    private val save: JButton = JButton("Save and Restart").also {
        it.addActionListener {
            val blackPlayer =
                blackSetButtons.elements.toList().first { it.isSelected }.toPlayerType(PieceColor.BlackSet)
            val whitePlayer =
                whiteSetButtons.elements.toList().first { it.isSelected }.toPlayerType(PieceColor.WhiteSet)

            isVisible = false
            saveCallback(YagocSettings(blackPlayer, whitePlayer))
        }
    }
    private val cancel: JButton = JButton("Cancel").also {
        it.addActionListener {
            isVisible = false
            cancelCallback()
        }
    }

    private val whiteSetButtons = ButtonGroup()
    private val blackSetButtons = ButtonGroup()

    fun createRadioButtonGroupings(yagocSettings: YagocSettings) {
        val blackSet: Array<JRadioButton> = arrayOf(
            JRadioButton(computerPlayerLabel, yagocSettings.blackPlayer.isComputer),
            JRadioButton(userPlayerLabel, yagocSettings.blackPlayer.isUser)
        )

        val whiteSet: Array<AbstractButton> = arrayOf(
            JRadioButton(computerPlayerLabel, yagocSettings.whitePlayer.isComputer),
            JRadioButton(userPlayerLabel, yagocSettings.whitePlayer.isUser)
        )

        JPanel().also {
            it.add(JLabel("Black Set"))
            for (i in blackSet.indices) {
                blackSetButtons.add(blackSet[i])
                it.add(blackSet[i])
            }
            contentPane.add(it)
        }

        JPanel().also {
            it.add(JLabel("White Set"))
            for (i in whiteSet.indices) {
                whiteSetButtons.add(whiteSet[i])
                it.add(whiteSet[i])
            }
            contentPane.add(it)
        }
    }

    inner class WindowListener(val callback: () -> Unit) : WindowAdapter() {
        override fun windowClosing(event: WindowEvent) {
            callback()
        }
    }

    private fun centerInScreen() {
        val dimension = toolkit.screenSize
        val x = ((dimension.getWidth() - width) / 2 - this.width).toInt()
        val y = ((dimension.getHeight() - height) / 2 - this.height).toInt()
        this.setLocation(x, y)
    }

    init {
        addWindowListener(WindowListener(cancelCallback))
        contentPane.layout = GridLayout(3, 0)
        createRadioButtonGroupings(currentSettings)
        JPanel().also {
            it.add(save)
            it.add(cancel)
            contentPane.add(it)
        }
        centerInScreen()
        pack()
        isVisible = true
    }

    private fun AbstractButton.toPlayerType(pieceColor: PieceColor): Player {
        return if (this.text == computerPlayerLabel) {
            MinimaxPlayer(pieceColor, 3, PlayerStrategy.F1)
        } else {
            UserPlayer(pieceColor)
        }
    }
}
