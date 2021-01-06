import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game extends JFrame {

    BoardController boardController;
    Container buttonContainer = Box.createVerticalBox();
    JButton b1 = new JButton("New Game ");
    JButton b2 = new JButton("Configure");
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

    public Game() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            boardController = new BoardController(images());

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());

            boardController.setLayout(new BorderLayout());
            boardController.setBackground(Color.lightGray);
            boardController.setPreferredSize(new Dimension(530, 520));

            cont.add(boardController, BorderLayout.WEST);

            b1.addActionListener(boardController::newBoard);
            b2.addActionListener(boardController::configurePlayers);
            b3.addActionListener(boardController::startGame);
            b4.addActionListener(this::load);
            b5.addActionListener(this::save);
            b6.addActionListener(this::exit);
            buttonContainer.setSize(new Dimension(120, 520));
            b1.setPreferredSize(new Dimension(110, 40));
            b2.setPreferredSize(new Dimension(110, 40));
            b3.setPreferredSize(new Dimension(110, 40));
            b4.setPreferredSize(new Dimension(110, 40));
            b5.setPreferredSize(new Dimension(110, 40));
            b6.setPreferredSize(new Dimension(110, 40));
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
            System.out.println("drawCounter =" + board.drawCounter);
            System.out.println("finished =" + board.finished);
            stream.close();
            fileStream.close();
        } catch (Exception exc) {
            System.out.println("Could not read file: " + exc);
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
            System.out.println("Saved game to " + absolutePath);
        } catch (Exception exc) {
            System.out.println("Could not write file:" + exc);
        }
    }

    private Image[] images() {
        Toolkit t = getToolkit();
        Image[] images = new Image[13];

        images[1] = t.getImage("img/peon.gif");
        images[2] = t.getImage("img/caballo.gif");
        images[3] = t.getImage("img/alfil.gif");
        images[4] = t.getImage("img/torre.gif");
        images[5] = t.getImage("img/reina.gif");
        images[6] = t.getImage("img/rey.gif");
        images[7] = t.getImage("img/peonNegro.gif");
        images[8] = t.getImage("img/caballoNegro.gif");
        images[9] = t.getImage("img/alfilNegro.gif");
        images[10] = t.getImage("img/torreNegro.gif");
        images[11] = t.getImage("img/reinaNegro.gif");
        images[12] = t.getImage("img/reyNegro.gif");

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
