package yagoc;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class Logger {
    private final JTextPane textPanel;
    private final SimpleAttributeSet info = info();
    private final SimpleAttributeSet warn = warn();

    Logger() {
        textPanel = null;
    }

    Logger(JTextPane textPanel) {
        this.textPanel = textPanel;
    }

    void info(String message) {
        if (this.textPanel != null) {
            try {
                textPanel.getStyledDocument().insertString(textPanel.getStyledDocument().getLength(), message + "\n", info);
                textPanel.setCaretPosition(textPanel.getDocument().getLength());
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(message);
        }
    }

    void warn(String message) {
        if (this.textPanel != null) {
            try {
                textPanel.getStyledDocument().insertString(textPanel.getStyledDocument().getLength(), message + "\n", warn);
                textPanel.setCaretPosition(textPanel.getDocument().getLength());
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println(message);
        }
    }

    private SimpleAttributeSet info() {
        return new SimpleAttributeSet();
    }

    private SimpleAttributeSet warn() {
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(attributeSet, true);

        return attributeSet;
    }
}
