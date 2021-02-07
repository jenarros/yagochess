package yagoc

import yagoc.board.Board
import yagoc.ui.UserOptionDialog
import yagoc.ui.YagocWindow
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