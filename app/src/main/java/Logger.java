import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Logger {
    private final JTextPane textPanel;
    private final SimpleAttributeSet info = info();
    private final SimpleAttributeSet warn = warn();

    public Logger(JTextPane textPanel) {
        this.textPanel = textPanel;
    }

    public void info(String message) {
        try {
            textPanel.getStyledDocument().insertString(textPanel.getStyledDocument().getLength(), message + "\n", info);
            textPanel.setCaretPosition(textPanel.getDocument().getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public void warn(String message) {
        try {
            textPanel.getStyledDocument().insertString(textPanel.getStyledDocument().getLength(), message + "\n", warn);
            textPanel.setCaretPosition(textPanel.getDocument().getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
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
