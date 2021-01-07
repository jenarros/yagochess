import javax.swing.*;

public class ChessGame {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error" + e);
		}

		Game game = new Game();
		game.setTitle("Yet Another Game Of Chess");
		game.setVisible(true);
	}
}