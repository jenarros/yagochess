import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game extends JFrame {

    static Image[] images = new Image[13];
    static BoardPanel boardPanel = new BoardPanel();
    Container buttonContainer = Box.createVerticalBox();
    Board board;
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
            Toolkit t;
            t = getToolkit();
            images[0] = null;
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

            Container cont = getContentPane();
            cont.setLayout(new BorderLayout());

            boardPanel.setLayout(new BorderLayout());
            boardPanel.setBackground(Color.lightGray);
            boardPanel.setPreferredSize(new Dimension(530, 520));

            cont.add(boardPanel, BorderLayout.WEST);

            boardPanel.addMouseListener(new AccionListener());

            b1.addActionListener(this::newBoard);
            b2.addActionListener(this::configurePlayers);
            b3.addActionListener(this::startGame);
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

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        } else if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
            e.getWindow().repaint();
        }
    }

    void load(ActionEvent e) {
        String path;

        try {
            FileDialog fDialogo = new FileDialog(this);
            fDialogo.setMode(FileDialog.LOAD);
            fDialogo.setVisible(true);
            path = fDialogo.getFile();
            FileInputStream fileStream = new FileInputStream(path);
            ObjectInputStream stream = new ObjectInputStream(fileStream);

            board = (Board) stream.readObject();
            boardPanel.set(board);
            boardPanel.repaint();
            System.out.println("drawCounter =" + board.drawCounter);
            System.out.println("finished =" + board.finished);
            stream.close();
            fileStream.close();
        } catch (Exception exc) {
            System.out.println("Could not read file: " + exc);
        }
    }

    void save(ActionEvent e) {
        String nombre;

        try {
            FileDialog fDialog = new FileDialog(this);
            fDialog.setMode(FileDialog.SAVE);
            fDialog.setVisible(true);
            nombre = fDialog.getFile();
            FileOutputStream fileStream = new FileOutputStream(nombre);
            ObjectOutputStream stream = new ObjectOutputStream(fileStream);

            stream.writeObject(board);
            stream.close();
            fileStream.close();
        } catch (Exception exc) {
            System.out.println("Could not write file:" + exc);
        }
    }

    void startGame(ActionEvent e) {
        if (board == null) {
            System.out.println("Primero debes crear el juego");
            return;
        }

        board.drawCounter = 0;
        System.out.println("drawCounter = " + board.drawCounter);
        board.finished = false;
        System.out.println("finished = " + board.finished);
        if (board.player1.type.equals("u") && board.player2.type.equals("m"))
            board.movePlayer2();
            //si los dos jugadores son máquinas dirigimos la partida desde aquí
        else if (board.player2.type.equals("m") && board.player1.type.equals("m")) {
            while (!board.finished) {
                try {
                    Thread.sleep(100);
                } catch (Exception exc) {
                    System.out.println("Error: " + exc);
                }

                board.movePlayer2();
                if (!board.finished)
                    board.movePlayer1();
            }
        }
    }

    void newBoard(ActionEvent e) {
        board = new Board();
        boardPanel.set(board);

        boardPanel.repaint();
    }

    void configurePlayers(ActionEvent e) {
        if (board == null) {
            System.out.println("Primero debes crear el juego");
            return;
        }
        int partida;
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("******************CONFIGURACION DE JUGADORES******************");
            System.out.println("Introduzca el tipo de partida:");
            System.out.println("Tipo 1: Jugador1 = maquina1:negras  &  Jugador2 = USUARIO1:blancas");
            System.out.println("Tipo 2: Jugador1 = USUARIO1:negras  &  Jugador2 = maquina1:blancas");
            System.out.println("Tipo 3: Jugador1 = maquina1:negras  &  Jugador2 = maquina2:blancas");
            System.out.println("Tipo 4: Jugador1 = USUARIO1:negras  &  Jugador2 = USUARIO2:blancas");
            do {
                System.out.print("Introduzca el tipo de partida:[ 1 | 2 | 3 | 4 ]  ");
                partida = Integer.parseInt(entrada.readLine());
            }
            while (partida < 1 || partida > 4);

            if (partida == 1) {
                board.player1 = new DefaultPlayer("maquina1", -1, "m");
                System.out.print("Introduzca el nivel del jugador 1: ");
                board.player1.level = Integer.parseInt(entrada.readLine());
                board.player2 = new DefaultPlayer("usuario1", 1, "u");
            } else if (partida == 2) {
                board.player1 = new DefaultPlayer("usuario1", -1, "u");
                board.player2 = new DefaultPlayer("maquina1", 1, "m");
                System.out.print("Introduzca el nivel del jugador 2: ");
                board.player2.level = Integer.parseInt(entrada.readLine());
            } else if (partida == 3) {
                board.player1 = new DefaultPlayer("maquina1", -1, "m");
                System.out.print("Introduzca el nivel del jugador 1: ");
                board.player1.level = Integer.parseInt(entrada.readLine());
                board.player2 = new DefaultPlayer("maquina2", 1, "m");
                System.out.print("Introduzca el nivel del jugador 2: ");
                board.player2.level = Integer.parseInt(entrada.readLine());
                board.player2.alphaBetaFunction = 2;
            } else {
                board.player1 = new DefaultPlayer("usuario1", -1, "u");
                board.player2 = new DefaultPlayer("usuario2", 1, "u");
            }
            System.out.println("JUGADOR \tNOMBRE\t\tTIPO\tNIVEL");
            System.out.println("1-NEGRAS\t" + board.player1.name + "\t" + board.player1.type + "\t" + board.player1.level);
            System.out.println("2-BLANCAS\t" + board.player2.name + "\t" + board.player2.type + "\t" + board.player2.level);
        } catch (Exception exc) {
            System.out.println("Error: " + exc);
            System.exit(-1);
        }
    }

    void exit(ActionEvent e) {
        System.exit(0);
    }

    class AccionListener implements MouseListener {
        public Point from, to;

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            Point position = e.getPoint();
            if ((position.getX() > 19) && (position.getX() < 501) &&
                    (position.getY() > 19) && (position.getY() < 501)) {
                from = board.boardSquare(position);
            }
        }

        public void mouseReleased(MouseEvent e) {
            Point position = e.getPoint();

            if ((position.getX() > 19) && (position.getX() < 501) &&
                    (position.getY() > 19) && (position.getY() < 501)) {
                to = board.boardSquare(position);
                board.movePiece(from, to);
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }
}
