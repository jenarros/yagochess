import javax.swing.*;

public class ChessGame {

	public ChessGame() {
		Game frame = new Game();
		frame.setTitle("Yet Another Game Of Chess");
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error" + e);
		}

		new ChessGame();
	}
}