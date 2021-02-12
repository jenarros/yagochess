package yagoc.ui

import yagoc.Yagoc.logger
import javax.swing.JOptionPane

class UserOptionDialog {
    fun gameType() = BoardPanel.GAME_OPTIONS[JOptionPane.showOptionDialog(
        null,
        """  black set  | white set
            1: machine   | user
            2: user      | machine
            3: machine 1 | machine 2
            4: user 1    | user 2
            """,
        "Choose type of game",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        BoardPanel.GAME_OPTIONS,
        BoardPanel.GAME_OPTIONS[0]
    )]

    fun getLevel(playerName: String, defaultOption: Int): Int {
        val level = BoardPanel.PLAYER_LEVELS[JOptionPane.showOptionDialog(
            null,
            "Select $playerName level: ",
            "Select player level",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            BoardPanel.PLAYER_LEVELS,
            defaultOption
        )]
        logger.info("level $level selected")
        return level
    }
}