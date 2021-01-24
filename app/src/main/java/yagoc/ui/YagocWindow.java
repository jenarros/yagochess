package yagoc.ui;

import yagoc.Board;
import yagoc.Controller;
import yagoc.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import static yagoc.Yagoc.logger;

public class YagocWindow extends JFrame {
    static final int BOARD_FONT_SIZE = 16;
    static final int MENU_FONT_SIZE = 12;
    static final int BORDER_SIZE = 20;
    static final int IMAGE_SIZE = 40;
    static final int SQUARE_SIZE = 60;
    static final int LOG_HEIGHT = 200;
    static final Color lightSquaresColor = new Color(138, 120, 93);
    static final Color darkSquaresColor = new Color(87, 58, 46);
    static final Color frameColor = Color.DARK_GRAY;

    private final BoardPanel boardPanel;
    private final Controller controller;

    public YagocWindow(Controller controller, Board board) {
        this.controller = controller;
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        JTextPane textPane = textpane();
        logger = new Logger(textPane);
        boardPanel = new BoardPanel(controller, board);

        centerInScreen();

        try {
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setPreferredSize(new Dimension(boardPanel.getBoardAndBorderSize(), LOG_HEIGHT));
            scrollPane.setMaximumSize(new Dimension(boardPanel.getBoardAndBorderSize(), LOG_HEIGHT));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());

            UIManager.put("OptionPane.messageFont", new Font(Font.MONOSPACED, Font.PLAIN, getMenuFontSize()));
            UIManager.put("OptionPane.buttonFont", new Font(Font.MONOSPACED, Font.PLAIN, getMenuFontSize()));

            cont.add(boardPanel, BorderLayout.WEST);
            cont.add(scrollPane, BorderLayout.SOUTH);
            setResizable(false);

            addMenuBar(this, controller);

            pack();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    private static void addMenuBar(YagocWindow yagocWindow, Controller controller) {
        JMenuItem restartGameMenuItem = new JMenuItem("Reset");
        restartGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, yagocWindow.getToolkit().getMenuShortcutKeyMaskEx()));
        restartGameMenuItem.addActionListener(e1 -> controller.newBoard());

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, yagocWindow.getToolkit().getMenuShortcutKeyMaskEx()));

        saveMenuItem.addActionListener(yagocWindow::save);

        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, yagocWindow.getToolkit().getMenuShortcutKeyMaskEx()));
        loadMenuItem.addActionListener(yagocWindow::open);

        JMenuItem optionsMenuItem = new JMenuItem("Preferences...");
        optionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, yagocWindow.getToolkit().getMenuShortcutKeyMaskEx()));
        optionsMenuItem.addActionListener(e1 -> controller.configurePlayers());

        JMenuItem undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, yagocWindow.getToolkit().getMenuShortcutKeyMaskEx()));
        undo.addActionListener(e -> controller.undo());

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menu.add(optionsMenuItem);
        menu.add(restartGameMenuItem);
        menu.add(undo);

        menuBar.add(menu);
        yagocWindow.setJMenuBar(menuBar);
    }

    private void centerInScreen() {
        Dimension dimension = getToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2 - boardPanel.getBoardAndBorderSize() / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2 - boardPanel.getBoardAndBorderSize() / 2);
        this.setLocation(x, y);
    }

    int getMenuFontSize() {
        return MENU_FONT_SIZE;
    }

    private JTextPane textpane() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        return textPane;
    }

    void open(ActionEvent e) {
        FileDialog fDialog = new FileDialog(this);
        fDialog.setMode(FileDialog.LOAD);
        fDialog.setVisible(true);

        if (fDialog.getFile() != null) {
            String absolutePath = fDialog.getDirectory() + fDialog.getFile();
            try (FileInputStream fileStream = new FileInputStream(absolutePath);
                 ObjectInputStream stream = new ObjectInputStream(fileStream)) {
                Board board = (Board) stream.readObject();
                controller.resetBoard(board);
            } catch (Exception exc) {
                logger.warn("Could not read file: " + exc);
            }
        }
    }

    void save(ActionEvent e) {
        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.SAVE);
            fDialog.setVisible(true);
            if (fDialog.getFile() != null) {
                String absolutePath = fDialog.getDirectory() + fDialog.getFile();
                controller.saveBoard(absolutePath);
                logger.info("Saved game to " + absolutePath);
            }
        } catch (Exception exc) {
            logger.warn("Could not write file:" + exc);
        }
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        } else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
            e.getWindow().repaint();
        }
    }
}
