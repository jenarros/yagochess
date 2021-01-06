import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BoardController extends JPanel {
	private final Board board;
	private final Image[] images;

	public BoardController(Image[] images) {
		this.images = images;
		this.board = new Board();
		addMouseListener(new AccionListener());
		startGame();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public Board getBoard() {
		return board;
	}

	void drawPiece(Graphics g, int x, int y, int piece) {
		Point position = toScreenCoordinates(x, y);
		if (piece < 0)
			piece = 6 - piece;
		g.drawImage(images[piece], position.x + 10, position.y + 10, 40, 40, this);
	}

	public Point toScreenCoordinates(int x, int y) {
		return new Point(y * 60 + 20, x * 60 + 20);
	}

	public void draw(Graphics g) {
		int x, y;
		int piece;

		for (x = 20; x < 390; x += 121)
			for (y = 20; y < 390; y += 121) {
				drawSquare(g, x, y, Color.white);
				drawSquare(g, x + 61, y, Color.blue);
				drawSquare(g, x, y + 61, Color.blue);
				drawSquare(g, x + 61, y + 61, Color.white);
			}
		for (x = 0; x < 8; x++)
			for (y = 0; y < 8; y++) {
				piece = board.tab[x][y];
				if (piece != 0) {
					drawPiece(g, x, y, piece);
				}
			}
	}

	private void drawSquare(Graphics g, int x, int y, Color colorin) {
		int[] coordX = new int[4];
		int[] coordY = new int[4];

		g.setColor(colorin);
		coordX[0] = x;
		coordX[1] = x;
		coordX[2] = x + 60;
		coordX[3] = x + 60;
		coordY[0] = y;
		coordY[1] = y + 60;
		coordY[2] = y + 60;
		coordY[3] = y;
		g.fillPolygon(coordX, coordY, 4);
	}

	void startGame(ActionEvent e) {
		startGame();
	}

	void startGame() {
		board.drawCounter = 0;
		System.out.println("drawCounter = " + board.drawCounter);
		board.finished = false;
		System.out.println("finished = " + board.finished);
		if (board.player1.type.equals("u") && board.player2.type.equals("m")) {
			board.movePlayer2();
			update();

		} else if (board.player2.type.equals("m") && board.player1.type.equals("m")) {
			while (!board.finished) {
				try {
					Thread.sleep(100);
				} catch (Exception exc) {
					System.out.println("Error: " + exc);
				}

				board.movePlayer2();
				update();
				if (!board.finished) {
					board.movePlayer1();
					update();
				}
			}
		}
	}

	void newBoard(ActionEvent e) {
		repaint();
	}

	void update() {
		update(getGraphics());
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

	public void resetBoard(Board board) {
		this.board.resetWith(board);
	}

	class AccionListener implements MouseListener {
		public Point from, to;

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			Point position = e.getPoint();
			if ((position.x > 19) && (position.x < 501) && (position.y > 19) && (position.y < 501)) {
				from = board.boardSquare(position);
			}
		}

		public void mouseReleased(MouseEvent e) {
			Point position = e.getPoint();

			if ((position.x > 19) && (position.x < 501) && (position.y > 19) && (position.y < 501)) {
				to = board.boardSquare(position);
				board.movePiece(from, to);
				update();
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
}
