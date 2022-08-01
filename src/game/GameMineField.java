package game;

import java.util.Random;

class GameMineField {
	private static int COLS = 14;
	private static int ROWS = 14;
	private static int N_BOMBS = 35;
	private static String[][] matrix;
	
	//CONSTRUCTOR
	public GameMineField() {
		matrix = new String[ROWS][COLS];
		Random random = new Random();
		String bomb = "| * |";
		
		// genero le N_BOMBS nella matrice
		Integer[] positions = new Integer[N_BOMBS]; // array di appoggio per tenere traccia delle poizioni delle bombe
		int index = 0;
		int new_position;
		
		for (int p=0; p<N_BOMBS; p++) {
			
			do { //ripeto finche non mi mette una bomba in una NUOVA posizione
				new_position = random.nextInt(ROWS*COLS-1) + 1; //nextInt(high-low) + low;
			}while(!is_new_bomb(positions, new_position));
			
			positions[p] = new_position;
			boolean find = false;
			
			for (int i=0; i<COLS; i++) {	
				for (int j=0; j<ROWS; j++) {	
					if (positions[p] == index) {
						matrix[i][j] = bomb;
						find = true; // bomba trovata
						break;
					}
					index++;
				}
				
				if (find) {
					index = 0;
					break;
				}
			}
		
		}
		
		
		// genero i numeri nella matrice
		int current_position = 0;
		for (int i=0; i<COLS; i++) {
			for (int j=0; j<ROWS; j++) {
				if (matrix[i][j] != bomb) { // non mi trovo sulla bomba
					if (j+1 < ROWS) if (matrix[i][j+1] == bomb) current_position++; // controllo a dx
					if (j-1 >= 0) 	if (matrix[i][j-1] == bomb) current_position++; // controllo a sx
					if (i+1 < COLS) if (matrix[i+1][j] == bomb) current_position++;	// controllo in basso
					if (i-1 >= 0) 	if (matrix[i-1][j] == bomb) current_position++; // controllo in alto
					if (i-1 >= 0 && j+1 < ROWS) 	if (matrix[i-1][j+1] == bomb) current_position++; // controllo in alto a dx
					if (i-1 >= 0 && j-1 >= 0) 		if (matrix[i-1][j-1] == bomb) current_position++; // controllo in alto a sx
					if (i+1 < COLS && j+1 < ROWS) 	if (matrix[i+1][j+1] == bomb) current_position++; // controllo in basso a dx
					if (i+1 < COLS && j-1 >= 0) 	if (matrix[i+1][j-1] == bomb) current_position++; // controllo in basso a sx
					
					matrix[i][j] = "| " + current_position + " |";
					current_position = 0;
				}
				
				
			}
			
		}
		
		
		// stampa
		for (int i=0; i<COLS; i++) {	
			for (int j=0; j<ROWS; j++) {	
				System.out.print("" + matrix[i][j]);
			}
			
			System.out.println();
		}
		
		
		
		 
	}
	
	public boolean is_new_bomb (Integer[] pos, Integer new_pos) {
		for (int i=0; i<pos.length; i++) {
			if (pos[i] == new_pos) return false;	
		}
		return true;
	}
}
