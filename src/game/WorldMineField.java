package game;

import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

class WorldMineField {
	private static int COLS = 20; //14
	private static int ROWS = 20;
	private static int N_BOMBS = COLS*ROWS*16/100; // la quantità di bombe è data circa dal 16% del numero totale di caselle (COLS*ROWS) 
	private static boolean dead;
	private static boolean finish;
	private static TileMineField[][] matrix;
	
	
	private BufferedImage bomb_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/bomb.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage flag_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/flag.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage pressed_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res//pressed.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage normal_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/normal.png"), TileMineField.getWidth(), TileMineField.getHeight());
	
	//CONSTRUCTOR
	public WorldMineField() {
		
		System.out.println("COLS*ROWS = " + COLS*ROWS);
		System.out.println("N_BOMBS = " + N_BOMBS);
		
		matrix = new TileMineField[ROWS][COLS];
		
		// costruisco ogni cella
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				matrix[i][j] = new TileMineField(i, j, normal_img, bomb_img, pressed_img, flag_img);
			}
			
		}
		
		reset(); //resetto il mondo e creo una nuova partita
		
	}
	
	private void place_all_bombs() {
		for (int i=0; i<N_BOMBS; i++) { 
			place_bomb();
		}
	}
	
	private void place_bomb() {
		Random random = new Random();
		int tileX = random.nextInt(COLS);
		int tileY = random.nextInt(ROWS);
		
		if (matrix[tileX][tileY].isBomb()) place_bomb();
		else {
			matrix[tileX][tileY].setBomb(true);
			return;
		}
		
	}
	
	private void set_numeber_of_near_bombs() {
		int number_of_near_bombs = 0;
		for (int i=0; i<COLS; i++) {
			for (int j=0; j<ROWS; j++) {
				if (!matrix[i][j].isBomb()) { // non mi trovo sulla bomba
					if (j+1 < ROWS) if (matrix[i][j+1].isBomb()) number_of_near_bombs++; // controllo a dx
					if (j-1 >= 0) 	if (matrix[i][j-1].isBomb()) number_of_near_bombs++; // controllo a sx
					if (i+1 < COLS) if (matrix[i+1][j].isBomb()) number_of_near_bombs++;	// controllo in basso
					if (i-1 >= 0) 	if (matrix[i-1][j].isBomb()) number_of_near_bombs++; // controllo in alto
					if (i-1 >= 0 && j+1 < ROWS) 	if (matrix[i-1][j+1].isBomb()) number_of_near_bombs++; // controllo in alto a dx
					if (i-1 >= 0 && j-1 >= 0) 		if (matrix[i-1][j-1].isBomb()) number_of_near_bombs++; // controllo in alto a sx
					if (i+1 < COLS && j+1 < ROWS) 	if (matrix[i+1][j+1].isBomb()) number_of_near_bombs++; // controllo in basso a dx
					if (i+1 < COLS && j-1 >= 0) 	if (matrix[i+1][j-1].isBomb()) number_of_near_bombs++; // controllo in basso a sx
					
					matrix[i][j].setAmountOfNearBombs(number_of_near_bombs);
					number_of_near_bombs = 0;
				}
				
			}
			
		}	
	}

	
	public void left_click(int x, int y) {
		if (dead == false && finish == false) {
			
			int x_axis = x/TileMineField.getWidth();
			int y_axis = y/TileMineField.getHeight();
			
			if (matrix[x_axis][y_axis].isOpened() == true) return;
			else if (matrix[x_axis][y_axis].isBomb()) dead = true;
			else if (matrix[x_axis][y_axis].isFlag()) return;
			else if (matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false) open(x_axis, y_axis);
			
			matrix[x_axis][y_axis].setOpened(true);
			
			checkFinish();
		}
	}
	
	public void right_click(int x, int y) {
		if(dead == false && finish == false){
			int x_axis = x/TileMineField.getWidth(); // ottengo la corretta posizione in base allo schermo
			int y_axis = y/TileMineField.getHeight();
			matrix[x_axis][y_axis].placeFlag(); //piazzo una flag nella posizione corretta
			
			checkFinish(); // controllo se il gioco è terminato
		}
		
	}
	
	
	private void checkFinish() {
		finish = true; 
		for(int i = 0; i<COLS; i++) {
			for(int j=0; j<ROWS; j++) {
				if (matrix[i][j].isBomb() && matrix[i][j].isOpened()) {
					dead = true;
					return;
				} 
				if (matrix[i][j].isBomb() == false && matrix[i][j].isOpened() == false) { // se trovo una casella non bomba non aperta allora non è terminato
					finish = false;
					return;
				}
			}
		}
		
		
	}
	
	public void reset() {
		// azzero il campo da gioco
		for(int i=0; i<COLS; i++){
			for(int j=0; j<ROWS; j++){
				matrix[i][j].reset();
			}
		}
		
		// azzero i booleani
		dead = false;
		finish = false;
		
		// ripiazzo le bombe e i numeri affianco
		place_all_bombs();
		set_numeber_of_near_bombs();
	}
	
	
	public void draw(Graphics g){
		Font font = new Font("SansSerif", 0, FrameMineField.getScreenWidth()*10/100); // la scritta sarà con una grandezza del 10% della finestra di gioco
		Rectangle rect = new Rectangle(FrameMineField.width, FrameMineField.height);
		for(int x = 0;x < COLS;x++){
			for(int y = 0;y < ROWS;y++){
				matrix[x][y].draw(g);
			}
		}
		
		if(dead){
			g.setColor(Color.RED);
			FrameMineField.drawCenteredString(g, "You're dead!", rect, font);
		}
		else if(finish){
			g.setColor(Color.GREEN);
			FrameMineField.drawCenteredString(g, "You Won!!", rect, font);
		}
	}
	
	
	
	//TODO: open()
	private void open(int x, int y) {
		matrix[x][y].setOpened(true);
		
		if(matrix[x] [y].getAmountOfNearBombs() == 0) {
			int mx = x - 1;
			int gx = x + 1;
			int my = y - 1;
			int gy = y + 1;			

			if(mx>=0 && my>=0 && matrix[mx][my].canOpen()) open(mx, my);
			if(mx>=0 && matrix[mx][y].canOpen()) open(mx, y);
			if(mx>=0 && gy<ROWS && matrix[mx][gy].canOpen()) open(mx, gy);
			
			if(my>=0 && matrix[x][my].canOpen()) open(x, my);
			if(gy<ROWS && matrix[x][gy].canOpen()) open(x, gy);
			
			if(gx<COLS && my>=0 && matrix[gx][my].canOpen()) open(gx, my);
			if(gx<COLS && matrix[gx][y].canOpen()) open(gx, y);
			if(gx<COLS && gy<ROWS && matrix[gx][gy].canOpen()) open(gx, gy);
		}
	}
	
	public static int getCOLS() {
		return COLS;
	}
	
	public static int getROWS() {
		return ROWS;
	} 
	
}
