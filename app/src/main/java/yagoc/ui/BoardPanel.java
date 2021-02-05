package yagoc.ui;

import yagoc.Controller;
import yagoc.board.Board;
import yagoc.board.Square;
import yagoc.pieces.Piece;
import yagoc.pieces.Pieces;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static yagoc.board.Move.FILE_NAMES;
import static yagoc.board.Move.RANK_NAMES;
import static yagoc.ui.YagocWindow.BOARD_FONT_SIZE;
import static yagoc.ui.YagocWindow.BORDER_SIZE;
import static yagoc.ui.YagocWindow.IMAGE_SIZE;
import static yagoc.ui.YagocWindow.LOG_HEIGHT;
import static yagoc.ui.YagocWindow.SQUARE_SIZE;
import static yagoc.ui.YagocWindow.darkSquaresColor;
import static yagoc.ui.YagocWindow.lightSquaresColor;

public class BoardPanel extends JPanel {
	public static final Integer[] PLAYER_LEVELS = {1, 2, 3, 4, 5};
	public static final Integer[] GAME_OPTIONS = {1, 2, 3, 4};
	public static final int REFRESH_RATE_MILLISECONDS = 20; // 1000 / rate = fps

	private final Board board;
	private final Controller controller;
	private final Map<Piece, Image> images = images();
	private final AccionListener mouseMotionListener = new AccionListener();

	public BoardPanel(Controller controller, Board board) {
		this.controller = controller;
		this.board = board;
		setLayout(new BorderLayout(0, 0));
		setBackground(YagocWindow.frameColor);
		setPreferredSize(new Dimension(getBoardAndBorderSize(), getBoardAndBorderSize()));
		setMaximumSize(new Dimension(getBoardAndBorderSize(), getBoardAndBorderSize()));
		setFont(new Font(Font.MONOSPACED, Font.BOLD, getBoardFontSize()));

		addMouseListener(mouseMotionListener);
		addMouseMotionListener(mouseMotionListener);
		new Timer(REFRESH_RATE_MILLISECONDS, (actionEvent) -> {
			repaint();
		}).start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintBoard(g);
	}

	void drawPiece(Graphics g, Square square, Piece piece) {
		drawPiece(g, toScreenCoordinates(square), piece, (getSquareSize() - getImageSize()) / 2);
	}

	void drawPiece(Graphics g, Point position, Piece piece, int gap) {
		g.drawImage(images.get(piece), position.x + gap, position.y + gap, getImageSize(), getImageSize(), this);
	}

	Point toScreenCoordinates(Square square) {
		return new Point(square.file() * getSquareSize() + getBorderSize(), square.rank() * getSquareSize() + getBorderSize());
	}

	void paintBoard(Graphics g) {
		drawBorder(g);

		Square.allSquares.forEach((square) -> {
			drawSquare(g, square);
		});

		if (mouseMotionListener.selectedSquare != null) drawSquare(g, mouseMotionListener.selectedSquare);
	}

	private void drawBorder(Graphics g) {
		g.setColor(Color.lightGray);
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

		if (!square.equals(mouseMotionListener.selectedSquare) && !piece.equals(Pieces.none)) {
			drawPiece(g, square, piece);
		} else if (square.equals(mouseMotionListener.selectedSquare) && mouseMotionListener.mousePosition != null) {
			drawPiece(g, toMouseLocation(mouseMotionListener.mousePosition), piece, 0);
		}
	}

	private Point toMouseLocation(Point mousePointer) {
		return new Point(mousePointer.x - getImageSize() / 2, mousePointer.y - getImageSize() / 2);
	}

	private Color squareColor(Square square) {
		if ((square.file() % 2 == 0 && square.rank() % 2 == 0) || square.file() % 2 == 1 && square.rank() % 2 == 1) {
			return lightSquaresColor;
		} else {
			return darkSquaresColor;
		}
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

	private Map<Piece, Image> images() {
		Toolkit t = getToolkit();
		Map<Piece, Image> images = new HashMap<>();

		images.put(Pieces.whitePawn, t.getImage(this.getClass().getResource("/img/peon.gif")));
		images.put(Pieces.whiteKnight, t.getImage(this.getClass().getResource("/img/caballo.gif")));
		images.put(Pieces.whiteBishop, t.getImage(this.getClass().getResource("/img/alfil.gif")));
		images.put(Pieces.whiteRook, t.getImage(this.getClass().getResource("/img/torre.gif")));
		images.put(Pieces.whiteQueen, t.getImage(this.getClass().getResource("/img/reina.gif")));
		images.put(Pieces.whiteKing, t.getImage(this.getClass().getResource("/img/rey.gif")));
		images.put(Pieces.blackPawn, t.getImage(this.getClass().getResource("/img/peonNegro.gif")));
		images.put(Pieces.blackKnight, t.getImage(this.getClass().getResource("/img/caballoNegro.gif")));
		images.put(Pieces.blackBishop, t.getImage(this.getClass().getResource("/img/alfilNegro.gif")));
		images.put(Pieces.blackRook, t.getImage(this.getClass().getResource("/img/torreNegro.gif")));
		images.put(Pieces.blackQueen, t.getImage(this.getClass().getResource("/img/reinaNegro.gif")));
		images.put(Pieces.blackKing, t.getImage(this.getClass().getResource("/img/reyNegro.gif")));

		return images;
	}

	class AccionListener implements MouseInputListener {
		Square selectedSquare;
		Point mousePosition;

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			Point position = e.getPoint();
			if (isInsideTheBoard(position)) {
				selectedSquare = boardSquare(position);
				mousePosition = e.getPoint();
			}
		}

		private boolean isInsideTheBoard(Point position) {
			return (position.x >= getBorderSize()) && (position.x < getBoardAndBorderSize() - getBorderSize()) &&
					(position.y >= getBorderSize()) && (position.y < getBoardAndBorderSize() - getBorderSize());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			Point position = e.getPoint();

			if (isInsideTheBoard(position)) {
				controller.move(selectedSquare, boardSquare(position));

				selectedSquare = null;
				mousePosition = null;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mousePosition = new Point(e.getPoint().x, e.getPoint().y);
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}
	}
}
