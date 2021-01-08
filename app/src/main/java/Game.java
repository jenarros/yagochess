import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

class Game extends JFrame {
    final static int BOARD_WIDTH = 530;
    final static int BOARD_HEIGHT = 520;
    final static int SIDEBAR_WIDTH = 120;
    private final Logger logger;

    BoardController boardController;
    Container buttonContainer = Box.createVerticalBox();
    JButton b1 = new JButton(" New Game ");
    JButton b2 = new JButton(" Configure");
    JButton b3 = new JButton("  Start  ");
    JButton b4 = new JButton("  Load   ");
    JButton b5 = new JButton("  Save   ");
    JButton b6 = new JButton("  Exit   ");
    Component espacio0 = Box.createGlue();
    Component espacio1 = Box.createGlue();
    Component espacio2 = Box.createGlue();
    Component espacio3 = Box.createGlue();
    Component espacio4 = Box.createGlue();
    Component espacio5 = Box.createGlue();
    Component espacio6 = Box.createGlue();

    Game() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        logger = new Logger(textPane);

        try {
            JScrollPane scrollPane = new JScrollPane(textPane);
            scrollPane.setPreferredSize(new Dimension(BOARD_WIDTH, 200));
            scrollPane.setMaximumSize(new Dimension(BOARD_WIDTH, 200));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());

            boardController = new BoardController(images(), logger);
            boardController.setLayout(new BorderLayout(1, 1));
            boardController.setBackground(Color.lightGray);
            boardController.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

            cont.add(boardController, BorderLayout.WEST);
            cont.add(scrollPane, BorderLayout.SOUTH);

            b1.addActionListener(boardController::newBoard);
            b2.addActionListener(boardController::configurePlayers);
            b3.addActionListener(boardController::startGame);
            b4.addActionListener(this::load);
            b5.addActionListener(this::save);
            b6.addActionListener(this::exit);

            Dimension buttonSize = new Dimension(SIDEBAR_WIDTH - 10, 40);
            buttonContainer.setSize(new Dimension(SIDEBAR_WIDTH, BOARD_HEIGHT));
            b1.setPreferredSize(buttonSize);
            b2.setPreferredSize(buttonSize);
            b3.setPreferredSize(buttonSize);
            b4.setPreferredSize(buttonSize);
            b5.setPreferredSize(buttonSize);
            b6.setPreferredSize(buttonSize);
            buttonContainer.add(espacio0);
            buttonContainer.add(b1);
            buttonContainer.add(espacio1);
            buttonContainer.add(b2);
            buttonContainer.add(espacio2);
            buttonContainer.add(b3);
            buttonContainer.add(espacio3);
            buttonContainer.add(b4);
            buttonContainer.add(espacio4);
            buttonContainer.add(b5);
            buttonContainer.add(espacio5);
            buttonContainer.add(b6);
            buttonContainer.add(espacio6);
            cont.add(buttonContainer, BorderLayout.EAST);

            pack();
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    void load(ActionEvent e) {
        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.LOAD);
            fDialog.setVisible(true);
            String absolutePath = fDialog.getDirectory() + fDialog.getFile();
            FileInputStream fileStream = new FileInputStream(absolutePath);
            ObjectInputStream stream = new ObjectInputStream(fileStream);

            Board board = (Board) stream.readObject();
            boardController.resetBoard(board);
            repaint();
            logger.info("drawCounter =" + board.drawCounter);
            logger.info("finished =" + board.finished);
            stream.close();
            fileStream.close();
        } catch (Exception exc) {
            logger.warn("Could not read file: " + exc);
        }
    }

    void save(ActionEvent e) {
        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.SAVE);
            fDialog.setVisible(true);
            String absolutePath = fDialog.getDirectory() + fDialog.getFile();
            FileOutputStream fileStream = new FileOutputStream(absolutePath);
            ObjectOutputStream stream = new ObjectOutputStream(fileStream);

            stream.writeObject(boardController.getBoard());
            stream.close();
            fileStream.close();
            logger.info("Saved game to " + absolutePath);
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

    void exit(ActionEvent e) {
        System.exit(0);
    }
}
