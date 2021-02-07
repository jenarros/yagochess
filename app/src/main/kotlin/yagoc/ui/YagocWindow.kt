package yagoc.ui

import yagoc.Controller
import yagoc.Logger
import yagoc.Yagoc.logger
import yagoc.board.Board
import yagoc.board.BoardView
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.io.FileInputStream
import java.io.ObjectInputStream
import javax.swing.*
import kotlin.system.exitProcess

class YagocWindow(private val controller: Controller, board: BoardView) : JFrame() {
    private val boardPanel: BoardPanel

    private fun centerInScreen() {
        val dimension = toolkit.screenSize
        val x = ((dimension.getWidth() - width) / 2 - boardPanel.boardAndBorderSize / 2).toInt()
        val y = ((dimension.getHeight() - height) / 2 - boardPanel.boardAndBorderSize / 2).toInt()
        this.setLocation(x, y)
    }

    private fun textpane(): JTextPane {
        val textPane = JTextPane()
        textPane.isEditable = false
        textPane.font = Font(Font.MONOSPACED, Font.PLAIN, 12)
        return textPane
    }

    fun open(e: ActionEvent) {
        val fDialog = FileDialog(this)
        fDialog.mode = FileDialog.LOAD
        fDialog.isVisible = true
        if (fDialog.file != null) {
            val absolutePath = fDialog.directory + fDialog.file
            try {
                FileInputStream(absolutePath).use { fileStream ->
                    ObjectInputStream(fileStream).use { stream ->
                        val board = stream.readObject() as Board
                        controller.resetBoard(board)
                    }
                }
            } catch (exc: Exception) {
                logger.warn("Could not read file: $exc")
            }
        }
    }

    fun save(e: ActionEvent) {
        try {
            val fDialog = FileDialog(this)
            fDialog.mode = FileDialog.SAVE
            fDialog.isVisible = true
            if (fDialog.file != null) {
                val absolutePath = fDialog.directory + fDialog.file
                controller.saveBoard(absolutePath)
                logger.info("Saved game to $absolutePath")
            }
        } catch (exc: Exception) {
            logger.warn("Could not write file:$exc")
        }
    }

    override fun processWindowEvent(e: WindowEvent) {
        super.processWindowEvent(e)
        if (e.id == WindowEvent.WINDOW_CLOSING) {
            exitProcess(0)
        } else if (e.id == WindowEvent.WINDOW_ACTIVATED) {
            e.window.repaint()
        }
    }

    companion object {
        const val BOARD_FONT_SIZE = 16
        const val menuFontSize = 12
        const val BORDER_SIZE = 20
        const val IMAGE_SIZE = 40
        const val SQUARE_SIZE = 60
        const val LOG_HEIGHT = 200
        val lightSquaresColor = Color(138, 120, 93)
        val darkSquaresColor = Color(87, 58, 46)
        val frameColor = Color.DARK_GRAY

        private fun addMenuBar(yagocWindow: YagocWindow, controller: Controller) {
            val restartGameMenuItem = JMenuItem("Reset")
            restartGameMenuItem.accelerator =
                KeyStroke.getKeyStroke(KeyEvent.VK_R, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            restartGameMenuItem.addActionListener { controller.newBoard() }
            val saveMenuItem = JMenuItem("Save")
            saveMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            saveMenuItem.addActionListener(yagocWindow::save)
            val loadMenuItem = JMenuItem("Open")
            loadMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            loadMenuItem.addActionListener(yagocWindow::open)
            val optionsMenuItem = JMenuItem("Preferences...")
            optionsMenuItem.accelerator =
                KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            optionsMenuItem.addActionListener { controller.configurePlayers() }
            val undo = JMenuItem("Undo")
            undo.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            undo.addActionListener { e: ActionEvent -> controller.undo() }
            val menuBar = JMenuBar()
            val menu = JMenu("File")
            menu.add(saveMenuItem)
            menu.add(loadMenuItem)
            menu.add(optionsMenuItem)
            menu.add(restartGameMenuItem)
            menu.add(undo)
            menuBar.add(menu)
            yagocWindow.jMenuBar = menuBar
        }
    }

    init {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK)
        val textPane = textpane()
        logger = Logger(textPane)
        boardPanel = BoardPanel(controller, board)
        centerInScreen()
        try {
            val scrollPane = JScrollPane(textPane)
            scrollPane.preferredSize = Dimension(boardPanel.boardAndBorderSize, LOG_HEIGHT)
            scrollPane.maximumSize = Dimension(boardPanel.boardAndBorderSize, LOG_HEIGHT)
            scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
            val cont = contentPane
            cont.layout = BorderLayout()
            UIManager.put("OptionPane.messageFont", Font(Font.MONOSPACED, Font.PLAIN, menuFontSize))
            UIManager.put("OptionPane.buttonFont", Font(Font.MONOSPACED, Font.PLAIN, menuFontSize))
            cont.add(boardPanel, BorderLayout.WEST)
            cont.add(scrollPane, BorderLayout.SOUTH)
            isResizable = false
            addMenuBar(this, controller)
            pack()
        } catch (e: Exception) {
            println("Error:$e")
        }
    }
}