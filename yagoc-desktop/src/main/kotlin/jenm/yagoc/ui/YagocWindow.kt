package jenm.yagoc.ui

import jenm.yagoc.Controller
import jenm.yagoc.Logger
import jenm.yagoc.Yagoc.logger
import jenm.yagoc.board.Board
import jenm.yagoc.board.BoardView
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.io.FileInputStream
import java.io.ObjectInputStream
import javax.swing.*
import kotlin.system.exitProcess

class YagocWindow(private val controller: Controller, board: BoardView) : JFrame() {
    private val boardPanel: BoardPanel = BoardPanel(controller, board)
    private val textPane = textpane().also {
        logger = Logger(it)
    }

    private val scrollPane = JScrollPane(textPane).also {
        it.preferredSize = Dimension(LOG_WIDTH, boardPanel.boardAndBorderSize / 2)
        it.maximumSize = Dimension(LOG_WIDTH, boardPanel.boardAndBorderSize / 2)
        it.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        it.isVisible = false
    }

    init {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK)
        centerInScreen()
        UIManager.put("OptionPane.messageFont", Font(Font.MONOSPACED, Font.PLAIN, menuFontSize))
        UIManager.put("OptionPane.buttonFont", Font(Font.MONOSPACED, Font.PLAIN, menuFontSize))
        contentPane.layout = BorderLayout()
        contentPane.add(boardPanel, BorderLayout.WEST)
        isResizable = false
        addMenuBar(this, controller)
        pack()
    }

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

    fun open() {
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

    fun save() {
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
        const val LOG_WIDTH = 400
        val lightSquaresColor = Color(138, 120, 93)
        val darkSquaresColor = Color(87, 58, 46)
        val frameColor = Color.DARK_GRAY

        private fun addMenuBar(yagocWindow: YagocWindow, controller: Controller) {
            val loadMenuItem = JMenuItem("Open")
            loadMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, yagocWindow.toolkit.menuShortcutKeyMask)
            loadMenuItem.addActionListener { yagocWindow.open() }
            val pauseMenuItem = JMenuItem("Pause")
            pauseMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, yagocWindow.toolkit.menuShortcutKeyMask)
            pauseMenuItem.addActionListener { controller.togglePause() }
            val restartGameMenuItem = JMenuItem("Reset")
            restartGameMenuItem.accelerator =
                KeyStroke.getKeyStroke(KeyEvent.VK_R, yagocWindow.toolkit.menuShortcutKeyMask)
            restartGameMenuItem.addActionListener { controller.newBoard() }
            val saveMenuItem = JMenuItem("Save")
            saveMenuItem.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, yagocWindow.toolkit.menuShortcutKeyMask)
            saveMenuItem.addActionListener { yagocWindow.save() }
            val optionsMenuItem = JMenuItem("Preferences...")
            optionsMenuItem.accelerator =
                KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, yagocWindow.toolkit.menuShortcutKeyMask)
            optionsMenuItem.addActionListener { controller.configurePlayers() }
            val undo = JMenuItem("Undo")
            undo.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, yagocWindow.toolkit.menuShortcutKeyMask)
            undo.addActionListener { e: ActionEvent -> controller.undo() }
            val menuBar = JMenuBar()
            val menu = JMenu("File")
            menu.add(loadMenuItem)
            menu.add(pauseMenuItem)
            menu.add(optionsMenuItem)
            menu.add(restartGameMenuItem)
            menu.add(saveMenuItem)
            menu.add(undo)
            menuBar.add(menu)
            val view = JMenu("View")
            val showLog = JMenuItem("Toggle Log")
            showLog.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_L, yagocWindow.toolkit.menuShortcutKeyMask)
            showLog.addActionListener { e: ActionEvent ->
                yagocWindow.toggleLog()
            }
            view.add(showLog)
            menuBar.add(view)
            yagocWindow.jMenuBar = menuBar
        }
    }

    fun toggleLog() {
        if (scrollPane.isVisible) {
            scrollPane.isVisible = false
            contentPane.remove(scrollPane)
            setSize(size.width - LOG_WIDTH, size.height)
        } else {
            scrollPane.isVisible = true
            contentPane.add(scrollPane, BorderLayout.EAST)
            setSize(size.width + LOG_WIDTH, size.height)
        }
    }
}