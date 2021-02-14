package jenm.yagoc.ui

import jenm.yagoc.Controller
import jenm.yagoc.board.*
import jenm.yagoc.pieces.*
import java.awt.*
import java.awt.event.MouseEvent
import java.util.*
import java.util.stream.IntStream
import javax.swing.JPanel
import javax.swing.Timer
import javax.swing.event.MouseInputListener
import kotlin.math.floor
import kotlin.math.min
class BoardPanel(private val controller: Controller, private val board: BoardView) : JPanel() {
    private val images = images()
    private val mouseMotionListener = AccionListener()

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        paintBoard(g)
    }

    fun drawPiece(g: Graphics, square: Square, piece: Piece) {
        drawPiece(g, toScreenCoordinates(square), piece, (squareSize - imageSize) / 2)
    }

    fun drawPiece(g: Graphics, position: Point, piece: Piece, gap: Int) {
        g.drawImage(images[piece], position.x + gap, position.y + gap, imageSize, imageSize, this)
    }

    fun toScreenCoordinates(square: Square): Point {
        return Point(square.file * squareSize + borderSize, square.rank * squareSize + borderSize)
    }

    fun paintBoard(g: Graphics) {
        drawBorder(g)
        allSquares.forEach { square: Square -> drawSquare(g, square) }
        mouseMotionListener.selectedSquare?.let { drawSquare(g, it) }
    }

    private fun drawBorder(g: Graphics) {
        g.color = Color.lightGray
        IntStream.range(0, 8).forEach { file: Int ->
            g.drawString(
                Move.FILE_NAMES[file],
                borderSize + (squareSize * 0.4).toInt() + file * squareSize,
                boardFontSize
            )
            g.drawString(
                Move.FILE_NAMES[file],
                borderSize + (squareSize * 0.4).toInt() + file * squareSize,
                boardSize + (borderSize * 1.8).toInt()
            )
        }
        IntStream.range(0, 8).forEach { rank: Int ->
            g.drawString(
                Move.RANK_NAMES[rank],
                (borderSize * 0.25).toInt(),
                borderSize + (squareSize * 0.6).toInt() + rank * squareSize
            )
            g.drawString(
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

    private fun drawSquare(graphics: Graphics, square: Square) {
        val piece = board.pieceAt(square)
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
            drawPiece(graphics, square, piece)
        } else if (square == mouseMotionListener.selectedSquare) {
            mouseMotionListener.mousePosition?.let {
                drawPiece(graphics, toMouseLocation(it), piece, 0)
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
            (dimension.height - YagocWindow.LOG_WIDTH) / originalBoardAndBorderSize,
            dimension.width / originalBoardAndBorderSize
        ) * value
    }

    val boardAndBorderSize = squareSize * 8 + borderSize * 2
    val boardSize = squareSize * 8
    val boardFontSize = scale(YagocWindow.BOARD_FONT_SIZE)

    private fun images(): Map<Piece, Image> =
        HashMap<Piece, Image>().also {
            it[whitePawn] = toolkit.getImage(this.javaClass.getResource("/img/white_pawn.gif"))
            it[whiteKnight] = toolkit.getImage(this.javaClass.getResource("/img/white_knight.gif"))
            it[whiteBishop] = toolkit.getImage(this.javaClass.getResource("/img/white_bishop.gif"))
            it[whiteRook] = toolkit.getImage(this.javaClass.getResource("/img/white_rook.gif"))
            it[whiteQueen] = toolkit.getImage(this.javaClass.getResource("/img/white_queen.gif"))
            it[whiteKing] = toolkit.getImage(this.javaClass.getResource("/img/white_king.gif"))
            it[blackPawn] = toolkit.getImage(this.javaClass.getResource("/img/black_pawn.gif"))
            it[blackKnight] = toolkit.getImage(this.javaClass.getResource("/img/black_knight.gif"))
            it[blackBishop] = toolkit.getImage(this.javaClass.getResource("/img/black_bishop.gif"))
            it[blackRook] = toolkit.getImage(this.javaClass.getResource("/img/black_rook.gif"))
            it[blackQueen] = toolkit.getImage(this.javaClass.getResource("/img/black_queen.gif"))
            it[blackKing] = toolkit.getImage(this.javaClass.getResource("/img/black_king.gif"))
        }


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
            val from = selectedSquare
            if (isInsideTheBoard(position)) {
                from?.let { controller.move(from, boardSquare(position)) }
                selectedSquare = null
                mousePosition = null
            }
        }

        override fun mouseEntered(e: MouseEvent) {}
        override fun mouseExited(e: MouseEvent) {}
        override fun mouseDragged(e: MouseEvent) {
            mousePosition = Point(e.point.x, e.point.y)
        }

        override fun mouseMoved(e: MouseEvent) {}
    }

    companion object {
        val PLAYER_LEVELS = arrayOf(1, 2, 3, 4, 5)
        val GAME_OPTIONS = arrayOf(1, 2, 3, 4)
        const val REFRESH_RATE_MILLISECONDS = 20 // 1000 / rate = fps
    }

    init {
        layout = BorderLayout(0, 0)
        background = YagocWindow.frameColor
        preferredSize = Dimension(boardAndBorderSize, boardAndBorderSize)
        maximumSize = Dimension(boardAndBorderSize, boardAndBorderSize)
        font = Font(Font.MONOSPACED, Font.BOLD, boardFontSize)
        addMouseListener(mouseMotionListener)
        addMouseMotionListener(mouseMotionListener)
        Timer(REFRESH_RATE_MILLISECONDS) { repaint() }.start()
    }
}