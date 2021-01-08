package yagoc;

import javax.swing.*;

public class Yagoc {
    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error" + e);
        }

        YagocUI yagocUI = new YagocUI();
        yagocUI.setTitle("Yet Another Game Of Chess");
        yagocUI.setVisible(true);
    }
}
