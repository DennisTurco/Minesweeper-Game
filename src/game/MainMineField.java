package game;

import javax.swing.JFrame;

class MainMineField {

	public static void main(String[] args) {
		
		/*String[][] matrix = new String[3][3];
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				matrix[i][j] = " ciao ";
			}
			
		}
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				System.out.print(matrix[i][j]);
			}
			
		}*/
		
		
		FrameMineField game = new FrameMineField();
		game.setVisible(true);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

}
