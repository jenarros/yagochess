package jenm.yagoc.ui.swing

import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

data class Logger(val textPanel: JTextPane) {
    private val infoAttributeSet = info()
    private val debugAttibuteSet = debug()

    fun info(message: String) {
        textPanel.styledDocument.insertString(textPanel.styledDocument.length, message + "\n", infoAttributeSet)
        textPanel.caretPosition = textPanel.document.length
        textPanel.update(textPanel.graphics)
    }

    fun debug(message: String) {
        textPanel.styledDocument.insertString(textPanel.styledDocument.length, message + "\n", debugAttibuteSet)
        textPanel.caretPosition = textPanel.document.length
        textPanel.update(textPanel.graphics)
    }

    private fun info(): SimpleAttributeSet {
        return SimpleAttributeSet()
    }

    private fun debug(): SimpleAttributeSet {
        val attributeSet = SimpleAttributeSet()
        StyleConstants.setBold(attributeSet, true)
        return attributeSet
    }
}