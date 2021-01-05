import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
	private Board board;

	public BoardPanel() {
		super();
		board = null;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (board != null)
			board.draw(g);
	}

	public void set(Board tab) {
		board = tab;
	}
}
