package jenm.yagoc.ui.swing

import javax.swing.JTextPane
import javax.swing.SwingUtilities
import javax.swing.text.SimpleAttributeSet

data class Logger(val textPanel: JTextPane) {
    private val infoAttributeSet = info()

    fun info(message: String) {
        textPanel.styledDocument.insertString(textPanel.styledDocument.length, message, infoAttributeSet)
        textPanel.caretPosition = textPanel.document.length
        SwingUtilities.invokeLater { textPanel.update(textPanel.graphics) }
    }

    fun debug(message: String) {
        println(message)
    }

    private fun info(): SimpleAttributeSet {
        return SimpleAttributeSet()
    }
}