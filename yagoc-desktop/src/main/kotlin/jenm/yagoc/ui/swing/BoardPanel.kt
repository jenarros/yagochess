package jenm.yagoc.ui.swing

import jenm.yagoc.Controller
import jenm.yagoc.board.*
import jenm.yagoc.pieces.Piece
import jenm.yagoc.pieces.PieceType
import jenm.yagoc.pieces.none
import java.awt.*
import java.awt.event.MouseEvent
import java.util.stream.IntStream
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.event.MouseInputListener
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class BoardPanel(private val controller: Controller, private val boardUpdate: () -> BoardView) : JPanel() {
    var theme = Themes.Modern
    var images = theme.loadImages()

    private val mouseMotionListener = AccionListener()

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        paintBoard(g)
    }

    fun gap(piece: Piece) =
        if (piece.pieceType == PieceType.Pawn) (squareSize - imageSize) / 2 else (squareSize - imageSize) / 4

    fun drawPiece(g: Graphics, position: Point, piece: Piece, gap: Int) {
        images[piece]?.let {
            g.drawImage(
                it, position.x + gap, position.y + gap,
                squareSize - gap * 2,
                squareSize - gap * 2, this
            )
        }
    }

    fun toScreenCoordinates(square: Square): Point {
        return Point(square.file * squareSize + borderSize, square.rank * squareSize + borderSize)
    }

    fun selectTheme(theme: Themes) {
        this.theme = theme
        this.images = theme.loadImages()
    }

    fun paintBoard(graphics: Graphics) {
        drawBorder(graphics)
        val board = boardUpdate()
        allSquares.forEach { square: Square -> drawSquare(graphics, square, board.pieceAt(square)) }
        mouseMotionListener.selectedSquare?.let { drawSquare(graphics, it, board.pieceAt(it)) }
    }

    private fun drawBorder(graphics: Graphics) {
        graphics.color = Color.lightGray
        IntStream.range(0, 8).forEach { file: Int ->
            graphics.drawString(
                Move.FILE_NAMES[file],
                borderSize + (squareSize * 0.4).toInt() + file * squareSize,
                boardFontSize
            )
            graphics.drawString(
                Move.FILE_NAMES[file],
                borderSize + (squareSize * 0.4).toInt() + file * squareSize,
                boardSize + (borderSize * 1.8).toInt()
            )
        }
        IntStream.range(0, 8).forEach { rank: Int ->
            graphics.drawString(
                Move.RANK_NAMES[rank],
                (borderSize * 0.25).toInt(),
                borderSize + (squareSize * 0.6).toInt() + rank * squareSize
            )
            graphics.drawString(
                Move.RANK_NAMES[rank],
                boardSize + (borderSize * 1.3).toInt(),
                borderSize + (squareSize * 0.6).toInt() + rank * squareSize
            )
        }
    }

    fun boardSquare(point: Point): Square {
        return square(
            floor(((point.y - borderSize) / squareSize).toDouble()).toInt(),
            floor(((point.x - borderSize) / squareSize).toDouble()).toInt()
        )
    }

    private fun drawSquare(graphics: Graphics, square: Square, piece: Piece) {
        val point = toScreenCoordinates(square)
        val coordX = IntArray(4)
        val coordY = IntArray(4)
        graphics.color = squareColor(square)
        coordX[0] = point.x
        coordX[1] = point.x
        coordX[2] = point.x + squareSize
        coordX[3] = point.x + squareSize
        coordY[0] = point.y
        coordY[1] = point.y + squareSize
        coordY[2] = point.y + squareSize
        coordY[3] = point.y
        graphics.fillPolygon(coordX, coordY, 4)
        if (square != mouseMotionListener.selectedSquare && piece != none) {
            drawPiece(graphics, toScreenCoordinates(square), piece, gap(piece))
        } else if (square == mouseMotionListener.selectedSquare) {
            mouseMotionListener.mousePosition?.let {
                drawPiece(graphics, toMouseLocation(it), piece, gap(piece))
            }
        }
    }

    private fun toMouseLocation(mousePointer: Point): Point {
        return Point(mousePointer.x - imageSize / 2, mousePointer.y - imageSize / 2)
    }

    private fun squareColor(square: Square): Color {
        return if (square.file % 2 == 0 && square.rank % 2 == 0 || square.file % 2 == 1 && square.rank % 2 == 1) {
            YagocWindow.lightSquaresColor
        } else {
            YagocWindow.darkSquaresColor
        }
    }

    val borderSize = scale(YagocWindow.BORDER_SIZE)
    val imageSize = scale(YagocWindow.IMAGE_SIZE)
    val squareSize = scale(YagocWindow.SQUARE_SIZE)

    fun scale(value: Int): Int {
        val dimension = toolkit.screenSize
        val originalBoardAndBorderSize = YagocWindow.SQUARE_SIZE * 8 + YagocWindow.BORDER_SIZE * 2
        return min(
            max(1,(dimension.height - YagocWindow.LOG_WIDTH) / originalBoardAndBorderSize),
            dimension.width / originalBoardAndBorderSize
        ) * value
    }

    val boardAndBorderSize = squareSize * 8 + borderSize * 2
    val boardSize = squareSize * 8
    val boardFontSize = scale(YagocWindow.BOARD_FONT_SIZE)

    internal inner class AccionListener : MouseInputListener {
        var selectedSquare: Square? = null
        var mousePosition: Point? = null
        override fun mouseClicked(e: MouseEvent) {}
        override fun mousePressed(e: MouseEvent) {
            val position = e.point
            if (isInsideTheBoard(position)) {
                selectedSquare = boardSquare(position)
                mousePosition = e.point
            }
        }

        private fun isInsideTheBoard(position: Point): Boolean {
            return position.x >= borderSize && position.x < boardAndBorderSize - borderSize &&
                    position.y >= borderSize && position.y < boardAndBorderSize - borderSize
        }

        override fun mouseReleased(e: MouseEvent) {
            val position = e.point
            if (isInsideTheBoard(position) && boardSquare(position) != selectedSquare) {
                val from = selectedSquare
                controller.uiAdapter.invokeLater {
                    from?.let {
                        controller.userMoves(from, boardSquare(position))
                        controller.uiAdapter.invokeLater { controller.computerMoves() }
                    }
                }
            }
            mousePosition = null
            selectedSquare = null
        }

        override fun mouseEntered(e: MouseEvent) {}
        override fun mouseExited(e: MouseEvent) {}
        override fun mouseDragged(e: MouseEvent) {
            mousePosition = Point(e.point.x, e.point.y)
        }

        override fun mouseMoved(e: MouseEvent) {}
    }

    init {
        layout = BorderLayout(0, 0)
        background = YagocWindow.frameColor
        preferredSize = Dimension(boardAndBorderSize, boardAndBorderSize)
        maximumSize = Dimension(boardAndBorderSize, boardAndBorderSize)
        font = Font(Font.MONOSPACED, Font.BOLD, boardFontSize)
        addMouseListener(mouseMotionListener)
        addMouseMotionListener(mouseMotionListener)
        Timer(REFRESH_RATE_MILLISECONDS) { SwingUtilities.invokeLater { repaint() } }.start()
    }

    companion object {
        const val REFRESH_RATE_MILLISECONDS = 40 // 1000 / rate = fps
    }
}