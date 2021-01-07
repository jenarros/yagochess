import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardController extends JPanel {
	private static final Integer[] playerLevels = {1, 2, 3, 4, 5};
	private static final Integer[] gameTypeOptions = {1, 2, 3, 4};

	private final Board board;
	private final Image[] images;
	private final Logger logger;

	public BoardController(Image[] images, Logger logger) {
		this.images = images;
		this.logger = logger;
		this.board = new Board(logger);
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
		board.finished = false;
		if (board.player1.isUser() && board.player2.isComputer()) {
			board.movePlayer2();
			update();

		} else if (board.player2.isComputer() && board.player1.isComputer()) {
			while (!board.finished) {
				try {
					Thread.sleep(200);
					board.movePlayer2();
					update();
					if (!board.finished) {
						Thread.sleep(200);
						board.movePlayer1();
						update();
					}
				} catch (Exception exc) {
					throw new RuntimeException(exc);
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
		try {
			int game = gameTypeOptions[JOptionPane.showOptionDialog(this,
					"  black set   | white set\n" +
							"1: machine   | user\n" +
							"2: user         | machine\n" +
							"3: machine 1 | machine 2\n" +
							"4: user 1       | user 2\n",
					"Choose type of game",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					gameTypeOptions,
					gameTypeOptions[0])];
			logger.info("Type " + game + " chosen");

			if (game == 1) {
				board.player1 = new ComputerPlayer("computer 1", -1, logger, getLevel("Select player 1 level: ", 1), PlayerStrategies.F1);
				board.player2 = new UserPlayer("user 1", 1);
			} else if (game == 2) {
				board.player1 = new UserPlayer("user 1", -1);
				board.player2 = new ComputerPlayer("computer 1", 1, logger, getLevel("Select player 2 level: ", 1), PlayerStrategies.F1);
			} else if (game == 3) {
				board.player1 = new ComputerPlayer("computer 1", -1, logger, getLevel("Select player 1 level: ", 1), PlayerStrategies.F1);
				board.player2 = new ComputerPlayer("computer 2", 1, logger, getLevel("Select player 2 level: ", 1), PlayerStrategies.F2);
			} else {
				board.player1 = new UserPlayer("user 1", -1);
				board.player2 = new UserPlayer("user 2", 1);
			}
			logger.info("Player\tname\ttype\tlevel");
			logger.info(" 1" + board.player1);
			logger.info(" 2" + board.player2);
		} catch (Exception exc) {
			logger.info("Error: " + exc);
			System.exit(-1);
		}
	}

	private int getLevel(String message, Integer defaultOption) {
		int level = playerLevels[JOptionPane.showOptionDialog(this,
				message,
				"Select player level",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				playerLevels,
				defaultOption)];
		logger.info("level " + level + " selected");
		return level;
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
