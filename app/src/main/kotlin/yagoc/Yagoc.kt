package yagoc;

import yagoc.board.Board;
import yagoc.ui.UserOptionDialog;
import yagoc.ui.YagocWindow;

import javax.swing.*;

public class Yagoc {
    public static Logger logger = new Logger();

    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error" + e);
        }

        Board board = new Board();
        YagocWindow yagocWindow = new YagocWindow(new Controller(board, new UserOptionDialog()), board);
        yagocWindow.setTitle("Yet Another Game Of Chess");
        yagocWindow.setVisible(true);
    }
}
