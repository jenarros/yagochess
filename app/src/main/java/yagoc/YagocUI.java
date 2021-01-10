package yagoc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import static yagoc.Yagoc.logger;

class YagocUI extends JFrame {
    static final int BOARD_FONT_SIZE = 16;
    static final int MENU_FONT_SIZE = 12;
    static final int BORDER_SIZE = 20;
    static final int IMAGE_SIZE = 40;
    static final int SQUARE_SIZE = 60;
    static final int LOG_HEIGHT = 200;
    static final Color lightSquaresColor = new Color(138, 120, 93);
    static final Color darkSquaresColor = new Color(87, 58, 46);
    static final Color frameColor = Color.DARK_GRAY;

    private final BoardController boardController;

    YagocUI() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        JTextPane textPane = textpane();
        logger = new Logger(textPane);
        boardController = new BoardController(images());

        addMenuBar(this, boardController);

        centerInScreen();

        try {
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setPreferredSize(new Dimension(boardController.getBoardAndBorderSize(), LOG_HEIGHT));
            scrollPane.setMaximumSize(new Dimension(boardController.getBoardAndBorderSize(), LOG_HEIGHT));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());

            UIManager.put("OptionPane.messageFont", new Font(Font.MONOSPACED, Font.PLAIN, getMenuFontSize()));
            UIManager.put("OptionPane.buttonFont", new Font(Font.MONOSPACED, Font.PLAIN, getMenuFontSize()));

            cont.add(boardController, BorderLayout.WEST);
            cont.add(scrollPane, BorderLayout.SOUTH);
            setResizable(false);

            pack();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    private void centerInScreen() {
        Dimension dimension = getToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2 - boardController.getBoardAndBorderSize() / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2 - boardController.getBoardAndBorderSize() / 2);
        this.setLocation(x, y);
    }

    private static void addMenuBar(YagocUI yagocUI, BoardController boardController) {
        JMenuItem restartGameMenuItem = new JMenuItem("Reset");
        restartGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, yagocUI.getToolkit().getMenuShortcutKeyMaskEx()));
        restartGameMenuItem.addActionListener(boardController::newBoard);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, yagocUI.getToolkit().getMenuShortcutKeyMaskEx()));

        saveMenuItem.addActionListener(yagocUI::save);

        JMenuItem loadMenuItem = new JMenuItem("Open");
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, yagocUI.getToolkit().getMenuShortcutKeyMaskEx()));
        loadMenuItem.addActionListener(yagocUI::open);

        JMenuItem optionsMenuItem = new JMenuItem("Preferences...");
        optionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, yagocUI.getToolkit().getMenuShortcutKeyMaskEx()));
        optionsMenuItem.addActionListener(boardController::configurePlayers);

        JMenuItem undo = new JMenuItem("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, yagocUI.getToolkit().getMenuShortcutKeyMaskEx()));
        undo.addActionListener(boardController::undo);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(saveMenuItem);
        menu.add(loadMenuItem);
        menu.add(optionsMenuItem);
        menu.add(restartGameMenuItem);
        menu.add(undo);

        menuBar.add(menu);
        yagocUI.setJMenuBar(menuBar);
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
        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.LOAD);
            fDialog.setVisible(true);
            if (fDialog.getFile() != null) {
                String absolutePath = fDialog.getDirectory() + fDialog.getFile();
                FileInputStream fileStream = new FileInputStream(absolutePath);
                ObjectInputStream stream = new ObjectInputStream(fileStream);

                Board board = (Board) stream.readObject();
                boardController.resetBoard(board);
                logger.info("drawCounter =" + board.drawCounter());
                logger.info("finished =" + board.hasFinished());
                stream.close();
                fileStream.close();
            }
        } catch (Exception exc) {
            logger.warn("Could not read file: " + exc);
        }
    }

    void save(ActionEvent e) {
        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.SAVE);
            fDialog.setVisible(true);
            if (fDialog.getFile() != null) {
                String absolutePath = fDialog.getDirectory() + fDialog.getFile();
                FileOutputStream fileStream = new FileOutputStream(absolutePath);
                ObjectOutputStream stream = new ObjectOutputStream(fileStream);

                stream.writeObject(boardController.getBoard());
                stream.close();
                fileStream.close();
                logger.info("Saved game to " + absolutePath);
            }
        } catch (Exception exc) {
            logger.warn("Could not write file:" + exc);
        }
    }

    private Map<Piece, Image> images() {
        Toolkit t = getToolkit();
        Map<Piece, Image> images = new HashMap<>();

        images.put(Piece.whitePawn, t.getImage("img/peon.gif"));
        images.put(Piece.whiteKnight, t.getImage("img/caballo.gif"));
        images.put(Piece.whiteBishop, t.getImage("img/alfil.gif"));
        images.put(Piece.whiteRook, t.getImage("img/torre.gif"));
        images.put(Piece.whiteQueen, t.getImage("img/reina.gif"));
        images.put(Piece.whiteKing, t.getImage("img/rey.gif"));
        images.put(Piece.blackPawn, t.getImage("img/peonNegro.gif"));
        images.put(Piece.blackKnight, t.getImage("img/caballoNegro.gif"));
        images.put(Piece.blackBishop, t.getImage("img/alfilNegro.gif"));
        images.put(Piece.blackRook, t.getImage("img/torreNegro.gif"));
        images.put(Piece.blackQueen, t.getImage("img/reinaNegro.gif"));
        images.put(Piece.blackKing, t.getImage("img/reyNegro.gif"));

        return images;
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
