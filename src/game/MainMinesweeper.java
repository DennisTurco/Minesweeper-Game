package game;

import javax.swing.JFrame;

class MainMinesweeper {

	public static void main(String[] args) {
		FrameMinesweeper game = new FrameMinesweeper();
		game.setVisible(true);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}