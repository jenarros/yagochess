package yagoc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
	static final int COMPUTER_PAUSE_SECONDS = 1;
	public static final int REFRESH_RATE_MILLISECONDS = 100;

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
		new Timer(REFRESH_RATE_MILLISECONDS, (actionEvent) -> {
			repaint();
		}).start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintBoard(g);
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

	void paintBoard(Graphics g) {
		Square.allSquares.forEach((square) -> {
			drawSquare(g, square);
		});

		IntStream.range(0, 8).forEach((file) -> {
			g.drawString(FILE_NAMES[file], getBorderSize() + (int) (getSquareSize() * 0.4) + file * getSquareSize(), getBoardFontSize());
			g.drawString(FILE_NAMES[file], getBorderSize() + (int) (getSquareSize() * 0.4) + file * getSquareSize(), getBoardSize() + (int) (getBorderSize() * 1.8));
		});

		IntStream.range(0, 8).forEach((rank) -> {
			g.drawString(RANK_NAMES[rank], (int) (getBorderSize() * 0.25), getBorderSize() + (int) (getSquareSize() * 0.6) + rank * getSquareSize());
			g.drawString(RANK_NAMES[rank], getBoardSize() + ((int) (getBorderSize() * 1.3)), getBorderSize() + (int) (getSquareSize() * 0.6) + rank * getSquareSize());
		});
	}

	Square boardSquare(Point p) {
		return new Square(Double.valueOf(Math.floor((p.y - getBorderSize()) / getSquareSize())).intValue(), Double.valueOf(Math.floor((p.x - getBorderSize()) / getSquareSize())).intValue());
	}

	private void drawSquare(Graphics g, Square square) {
		Piece piece = board.pieceAt(square);
		Point point = toScreenCoordinates(square);
		int[] coordX = new int[4];
		int[] coordY = new int[4];

		g.setColor(squareColor(square));
		coordX[0] = point.x;
		coordX[1] = point.x;
		coordX[2] = point.x + getSquareSize();
		coordX[3] = point.x + getSquareSize();
		coordY[0] = point.y;
		coordY[1] = point.y + getSquareSize();
		coordY[2] = point.y + getSquareSize();
		coordY[3] = point.y;
		g.fillPolygon(coordX, coordY, 4);
		if (!(piece == Piece.none)) drawPiece(g, square, piece);
	}

	private Color squareColor(Square square) {
		if ((square.file % 2 == 0 && square.rank % 2 == 0) || square.file % 2 == 1 && square.rank % 2 == 1) {
			return lightSquaresColor;
		} else {
			return darkSquaresColor;
		}
	}

	void nextMove() {
		if (board.hasFinished()) return;

		if (board.currentPlayer() == board.blackPlayer()) {
			board.movePlayer(board.blackPlayer());
		} else {
			board.movePlayer(board.whitePlayer());
		}

		if (board.currentPlayer().isComputer()) breath();

		if ((board.currentPlayer() == board.blackPlayer() && board.blackPlayer().isComputer())
				|| (board.currentPlayer() == board.whitePlayer() && board.whitePlayer().isComputer()))
			SwingUtilities.invokeLater(this::nextMove);
	}

	private void breath() {
		try {
			TimeUnit.SECONDS.sleep(COMPUTER_PAUSE_SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	void newBoard(ActionEvent unused) {
		board.reset();
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
				board.blackPlayer(new ComputerPlayer("computer", SetType.blackSet, getLevel("computer", 1), PlayerStrategy.F1));
				board.whitePlayer(new UserPlayer("user", SetType.whiteSet));
			} else if (game == 2) {
				board.blackPlayer(new UserPlayer("user", SetType.blackSet));
				board.whitePlayer(new ComputerPlayer("computer", SetType.whiteSet, getLevel("computer", 1), PlayerStrategy.F1));
			} else if (game == 3) {
				board.blackPlayer(new ComputerPlayer("computer 1", SetType.blackSet, getLevel("computer 1", 1), PlayerStrategy.F1));
				board.whitePlayer(new ComputerPlayer("computer 2", SetType.whiteSet, getLevel("computer 2", 1), PlayerStrategy.F2));
			} else {
				board.blackPlayer(new UserPlayer("user 1", SetType.blackSet));
				board.whitePlayer(new UserPlayer("user 2", SetType.whiteSet));
			}
			logger.info("name\ttype\tlevel");
			logger.info(board.blackPlayer().toString());
			logger.info(board.whitePlayer().toString());
			// if the player 2 (whiteSet) is a computer then start automatically
			if (board.whitePlayer().isComputer()) {
				SwingUtilities.invokeLater(this::nextMove);
			}
		} catch (Exception exc) {
			logger.info("Error: " + exc);
			System.exit(-1);
		}
	}

	private int getLevel(String playerName, Integer defaultOption) {
		int level = PLAYER_LEVELS[JOptionPane.showOptionDialog(this,
				"Select " + playerName + " level: ",
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
		board.resetWith(board);
		nextMove();
	}

	void undo(ActionEvent unused) {
		if (!checkpoints.isEmpty()) {
			this.board.resetWith(checkpoints.remove(checkpoints.size() - 1));
		}
	}

	class AccionListener implements MouseListener {
		Square from;

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
				Board copy = board.copy();

				final boolean hasMoved = board.moveIfPossible(from, boardSquare(position));

				SwingUtilities.invokeLater(() -> {
					if (hasMoved) {
						checkpoints.add(copy);
					}
					nextMove();
				});
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
}
