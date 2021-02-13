package jenm.yagoc

import jenm.yagoc.board.Board
import jenm.yagoc.ui.UserOptionDialog
import jenm.yagoc.ui.YagocWindow
import javax.swing.UIManager

object Yagoc {
    @JvmField
    var logger = Logger()

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        val board = Board()
        val yagocWindow = YagocWindow(Controller(board, UserOptionDialog()), board)
        yagocWindow.title = "Yet Another Game Of Chess"
        yagocWindow.isVisible = true
    }
}