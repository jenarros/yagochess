package yagoc.ui;

import javax.swing.*;

import static yagoc.Yagoc.logger;
import static yagoc.ui.BoardPanel.GAME_OPTIONS;
import static yagoc.ui.BoardPanel.PLAYER_LEVELS;

public class UserOptionDialog {
    public int getGameType() {
        int game = GAME_OPTIONS[JOptionPane.showOptionDialog(null,
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
        return game;
    }


    public int getLevel(String playerName, Integer defaultOption) {
        int level = PLAYER_LEVELS[JOptionPane.showOptionDialog(null,
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
}
