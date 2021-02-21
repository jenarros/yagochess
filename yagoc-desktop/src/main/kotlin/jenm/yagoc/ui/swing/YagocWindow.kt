package jenm.yagoc.ui.swing

import jenm.yagoc.Controller
import jenm.yagoc.Yagoc.logger
import jenm.yagoc.board.Board
import jenm.yagoc.board.BoardView
import jenm.yagoc.defaultSettings
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.WindowEvent
import java.io.FileInputStream
import java.io.ObjectInputStream
import javax.swing.*
import kotlin.system.exitProcess


class YagocWindow(private val controller: Controller, board: BoardView, textPane: JTextPane) : JFrame() {
    private val iconImagePath = "/themes/original/black_pawn.gif"
    private val boardPanel: BoardPanel = BoardPanel(controller, board)
    private var settings = defaultSettings
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
        contentPane.add(scrollPane, BorderLayout.EAST)
        isResizable = false
        addMenuBar(this, controller)
        iconImage = toolkit.getImage(this.javaClass.getResource(iconImagePath))
        Taskbar.getTaskbar().iconImage = toolkit.getImage(this.javaClass.getResource(iconImagePath))
        pack()
    }

    private fun centerInScreen() {
        val dimension = toolkit.screenSize
        val x = ((dimension.getWidth() - width) / 2 - boardPanel.boardAndBorderSize / 2).toInt()
        val y = ((dimension.getHeight() - height) / 2 - boardPanel.boardAndBorderSize / 2).toInt()
        this.setLocation(x, y)
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
                logger.debug("Could not read file: $exc")
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
            logger.debug("Could not write file:$exc")
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

    private fun addMenuBar(yagocWindow: YagocWindow, controller: Controller) {
        val loadMenuItem = JMenuItem("Open").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_O, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { yagocWindow.open() }
        }
        val pauseMenuItem = JMenuItem("Pause").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_P, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { controller.togglePause() }
        }
        val restartGameMenuItem = JMenuItem("Reset").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_R, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { controller.newBoard() }
        }
        val saveMenuItem = JMenuItem("Save").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { yagocWindow.save() }
        }
        val optionsMenuItem = JMenuItem("Preferences...").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener {
                controller.pause()
                PreferencesPanel(yagocWindow, yagocWindow.settings, { yagocSettings ->
                    yagocWindow.settings = yagocSettings
                    SwingUtilities.invokeLater {
                        controller.configurePlayers(yagocSettings)
                        controller.resume()
                    }
                }, {
                    controller.resume()
                })
            }
        }
        val undo = JMenuItem("Undo").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_Z, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { controller.undo() }
        }
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
        val showLog = JMenuItem("Toggle Log").also {
            it.accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_L, yagocWindow.toolkit.menuShortcutKeyMaskEx)
            it.addActionListener { yagocWindow.toggleLog() }
        }
        view.add(showLog)
        val themes = JMenu("Themes")
        val themeGroup = ButtonGroup()
        val modernMenuItem = JRadioButtonMenuItem("Modern", true).also {
            it.addActionListener {
                boardPanel.selectTheme(Themes.Modern)
            }
            themes.add(it)
        }
        themeGroup.add(modernMenuItem)
        val originalMenuItem = JRadioButtonMenuItem("Original").also {
            it.addActionListener {
                boardPanel.selectTheme(Themes.Original)
            }
            themes.add(it)
        }
        themeGroup.add(originalMenuItem)

        view.add(themes)
        menuBar.add(view)
        yagocWindow.jMenuBar = menuBar
    }

    fun toggleLog() {
        if (scrollPane.isVisible) {
            scrollPane.isVisible = false
            setSize(size.width - LOG_WIDTH, size.height)
        } else {
            scrollPane.isVisible = true
            setSize(size.width + LOG_WIDTH, size.height)
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
    }
}