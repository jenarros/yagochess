package yagoc

import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

class Logger {
    private val textPanel: JTextPane?
    private val info = info()
    private val warn = warn()

    constructor() {
        textPanel = null
    }

    constructor(textPanel: JTextPane?) {
        this.textPanel = textPanel
    }

    fun info(message: String) {
        if (textPanel != null && textPanel.graphics != null) {
            textPanel.styledDocument.insertString(textPanel.styledDocument.length, message + "\n", info)
            textPanel.caretPosition = textPanel.document.length
            textPanel.update(textPanel.graphics)
        } else {
            println(message)
        }
    }

    fun warn(message: String) {
        if (textPanel != null && textPanel.graphics != null) {
            textPanel.styledDocument.insertString(textPanel.styledDocument.length, message + "\n", warn)
            textPanel.caretPosition = textPanel.document.length
            textPanel.update(textPanel.graphics)
        } else {
            System.err.println(message)
        }
    }

    private fun info(): SimpleAttributeSet {
        return SimpleAttributeSet()
    }

    private fun warn(): SimpleAttributeSet {
        val attributeSet = SimpleAttributeSet()
        StyleConstants.setBold(attributeSet, true)
        return attributeSet
    }
}