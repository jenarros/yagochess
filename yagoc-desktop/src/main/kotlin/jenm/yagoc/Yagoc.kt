package jenm.yagoc

import jenm.yagoc.board.Board
import jenm.yagoc.ui.swing.Logger
import jenm.yagoc.ui.swing.SwingUIAdapter
import jenm.yagoc.ui.swing.YagocWindow
import java.awt.Font
import javax.swing.JTextPane
import javax.swing.UIManager

object Yagoc {
    private val textPane = JTextPane().also {
        it.isEditable = false
        it.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
    }

    val logger = Logger(textPane)

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val board = Board(defaultSettings)
        val yagocWindow = YagocWindow(Controller(board, SwingUIAdapter()), board, textPane)
        yagocWindow.title = "Yet Another Game Of Chess"
        yagocWindow.isVisible = true
    }
}