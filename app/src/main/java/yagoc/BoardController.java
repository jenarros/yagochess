package yagoc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.IntStream;

import static yagoc.Yagoc.logger;
import static yagoc.YagocUI.BOARD_FONT_SIZE;
import static yagoc.YagocUI.BORDER_SIZE;
import static yagoc.YagocUI.IMAGE_SIZE;
import static yagoc.YagocUI.LOG_HEIGHT;
import static yagoc.YagocUI.SQUARE_SIZE;
import static yagoc.YagocUI.darkSquaresColor;
import static yagoc.YagocUI.lightSquaresColor;

class BoardController extends JPanel {
	static final Integer[] PLAYER_LEVELS = {1, 2, 3, 4, 5};
	static final Integer[] GAME_OPTIONS = {1, 2, 3, 4};
	static final String[] FILE_NAMES = {"a", "b", "c", "d", "e", "f", "g", "h"};
	static final String[] RANK_NAMES = {"8", "7", "6", "5", "4", "3", "2", "1"};

	private final Board board;
	private final Map<Piece, Image> images;
	private final ArrayList<Board> checkpoints = new ArrayList<>();

	BoardController(Map<Piece, Image> images) {
		this.images = images;
		this.board = new Board();
		setLayout(new BorderLayout(0, 0));
		setBackground(YagocUI.frameColor);
		setPreferredSize(new Dimension(getBoardAndBorderSize(), getBoardAndBorderSize()));
		setMaximumSize(new Dimension(getBoardAndBorderSize(), getBoardAndBorderSize()));
		setFont(new Font(Font.MONOSPACED, Font.BOLD, getBoardFontSize()));

		addMouseListener(new AccionListener());
		startGame();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	Board getBoard() {
		return board;
	}

	void drawPiece(Graphics g, Square square, Piece piece) {
		Point position = toScreenCoordinates(square);
		int gap = (getSquareSize() - getImageSize()) / 2;
		g.drawImage(images.get(piece), position.x + gap, position.y + gap, getImageSize(), getImageSize(), this);
	}

	Point toScreenCoordinates(Square square) {
		return new Point(square.file * getSquareSize() + getBorderSize(), square.rank * getSquareSize() + getBorderSize());
	}

	void draw(Graphics g) {
		for (int x = getBorderSize(); x < getBoardSize(); x += getSquareSize() * 2)
			for (int y = getBorderSize(); y < getBoardSize(); y += getSquareSize() * 2) {
				drawSquare(g, x, y, lightSquaresColor);
				drawSquare(g, x + getSquareSize(), y, darkSquaresColor);
				drawSquare(g, x, y + getSquareSize(), darkSquaresColor);
				drawSquare(g, x + getSquareSize(), y + getSquareSize(), lightSquaresColor);
			}

		IntStream.range(0, 8).forEach((file) -> {
			g.drawString(FILE_NAMES[file], getBorderSize() + (int) (getSquareSize() * 0.4) + file * getSquareSize(), getBoardFontSize());
			g.drawString(FILE_NAMES[file], getBorderSize() + (int) (getSquareSize() * 0.4) + file * getSquareSize(), getBoardSize() + (int) (getBorderSize() * 1.8));
		});

		IntStream.range(0, 8).forEach((rank) -> {
			g.drawString(RANK_NAMES[rank], (int) (getBorderSize() * 0.25), getBorderSize() + (int) (getSquareSize() * 0.6) + rank * getSquareSize());
			g.drawString(RANK_NAMES[rank], getBoardSize() + ((int) (getBorderSize() * 1.3)), getBorderSize() + (int) (getSquareSize() * 0.6) + rank * getSquareSize());
		});

		Square.allSquares.forEach((square) -> {
			Piece piece = board.get(square);
			if (piece != Piece.none) {
				drawPiece(g, square, piece);
			}
		});
	}

	private void drawSquare(Graphics g, int x, int y, Color color) {
		int[] coordX = new int[4];
		int[] coordY = new int[4];

		g.setColor(color);
		coordX[0] = x;
		coordX[1] = x;
		coordX[2] = x + getSquareSize();
		coordX[3] = x + getSquareSize();
		coordY[0] = y;
		coordY[1] = y + getSquareSize();
		coordY[2] = y + getSquareSize();
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
		board.reset();
		repaint();
	}

	void update() {
		update(getGraphics());
	}

	void configurePlayers(ActionEvent e) {
		try {
			int game = GAME_OPTIONS[JOptionPane.showOptionDialog(this,
					"  black set  | white set\n" +
							"1: machine   | user\n" +
							"2: user      | machine\n" +
							"3: machine 1 | machine 2\n" +
							"4: user 1    | user 2\n",
					"Choose type of game",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					GAME_OPTIONS,
					GAME_OPTIONS[0])];
			logger.info("Type " + game + " chosen");

			if (game == 1) {
				board.player1 = new ComputerPlayer("computer", SetType.blackSet, getLevel(board.player1, 1), PlayerStrategy.F1);
				board.player2 = new UserPlayer("user", SetType.whiteSet);
			} else if (game == 2) {
				board.player1 = new UserPlayer("user", SetType.blackSet);
				board.player2 = new ComputerPlayer("computer", SetType.whiteSet, getLevel(board.player2, 1), PlayerStrategy.F1);
			} else if (game == 3) {
				board.player1 = new ComputerPlayer("computer 1", SetType.blackSet, getLevel(board.player1, 1), PlayerStrategy.F1);
				board.player2 = new ComputerPlayer("computer 2", SetType.whiteSet, getLevel(board.player2, 1), PlayerStrategy.F2);
			} else {
				board.player1 = new UserPlayer("user 1", SetType.blackSet);
				board.player2 = new UserPlayer("user 2", SetType.whiteSet);
			}
			logger.info("name\ttype\tlevel");
			logger.info(board.player1.toString());
			logger.info(board.player2.toString());
		} catch (Exception exc) {
			logger.info("Error: " + exc);
			System.exit(-1);
		}
	}

	private int getLevel(Player player, Integer defaultOption) {
		int level = PLAYER_LEVELS[JOptionPane.showOptionDialog(this,
				"Select " + player.name() + " level: ",
				"Select player level",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				PLAYER_LEVELS,
				defaultOption)];
		logger.info("level " + level + " selected");
		return level;
	}


	int getBorderSize() {
		return scale(BORDER_SIZE);
	}

	int getImageSize() {
		return scale(IMAGE_SIZE);
	}

	int getSquareSize() {
		return scale(SQUARE_SIZE);
	}

	int scale(int value) {
		Dimension dimension = getToolkit().getScreenSize();
		int originalBoardAndBorderSize = SQUARE_SIZE * 8 + BORDER_SIZE * 2;
		return Math.min((dimension.height - LOG_HEIGHT) / originalBoardAndBorderSize, dimension.width / originalBoardAndBorderSize) * value;
	}

	int getBoardAndBorderSize() {
		return getSquareSize() * 8 + getBorderSize() * 2;
	}

	int getBoardSize() {
		return getSquareSize() * 8;
	}

	int getBoardFontSize() {
		return scale(BOARD_FONT_SIZE);
	}

	void resetBoard(Board board) {
		checkpoints.clear();
		this.board.resetWith(board);
	}

	void undo(ActionEvent actionEvent) {
		if (!checkpoints.isEmpty()) {
			this.board.resetWith(checkpoints.remove(checkpoints.size() - 1));
			repaint();
		}
	}

	class AccionListener implements MouseListener {
		Square from, to;

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			Point position = e.getPoint();
			if (isInsideTheBoard(position)) {
				from = boardSquare(position);
			}
		}

		private boolean isInsideTheBoard(Point position) {
			return (position.x >= getBorderSize()) && (position.x < getBoardAndBorderSize() - getBorderSize()) &&
					(position.y >= getBorderSize()) && (position.y < getBoardAndBorderSize() - getBorderSize());
		}

		public void mouseReleased(MouseEvent e) {
			Point position = e.getPoint();

			if (isInsideTheBoard(position)) {
				to = boardSquare(position);
				Board copy = board.copy();

				if (board.moveIfPossible(from, to)) {
					checkpoints.add(copy);
					update();
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		Square boardSquare(Point p) {
			return new Square(Double.valueOf(Math.floor((p.y - getBorderSize()) / getSquareSize())).intValue(), Double.valueOf(Math.floor((p.x - getBorderSize()) / getSquareSize())).intValue());
		}
	}
}
