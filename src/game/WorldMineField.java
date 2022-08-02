package game;

import java.util.Random;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;

class WorldMineField {
	private static int COLS = 14;
	private static int ROWS = 14;
	private static int N_BOMBS = 35;
	private static boolean dead;
	private static boolean finish;
	private static TileMineField[][] matrix;
	
	
	private BufferedImage bomb_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/bomb.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage flag_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/flag.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage pressed_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/pressed.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage normal_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/normal.png"), TileMineField.getWidth(), TileMineField.getHeight());
	
	//CONSTRUCTOR
	public WorldMineField() {
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
	
	public boolean is_dead(TileMineField[][] matrix, int current_position) {
		int index = 0;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (current_position == index && matrix[i][j].isBomb()) {
					return true;
				}
				index++;
			}
		}
		return false;
	}
	
	public boolean is_alive(TileMineField[][] matrix, int current_position) {
		if (is_dead(matrix, current_position)) return false;
		else return true;
	}
	
	//TODO: left_click()
	public void left_click(int x, int y) {
		if(dead == false && finish == false) {
			int tileX = x/TileMineField.getWidth();
			int tileY = y/TileMineField.getHeight();
			
			if(matrix[tileX][tileY].isFlag() == false) {
				matrix[tileX][tileY].setOpened(true);
				
				if(matrix[tileX][tileY].isBomb()) dead = true;
				else {
					if(matrix[tileX][tileY].getAmountOfNearBombs() == 0) {
						open(tileX, tileY);
					}
				}
				
				checkFinish();
			}
		}
	}
	
	//TODO: right_click()
	public void right_click(int x, int y) {
		if(dead == false && finish == false){
			int tileX = x/TileMineField.getWidth(); // ottengo la corretta posizione in base allo schermo
			int tileY = y/TileMineField.getHeight();
			matrix[tileX][tileY].placeFlag(); //piazzo una flag nella posizione corretta
			
			checkFinish(); // controllo se il gioco è terminato
		}
		
	}
	
	//TODO: checkFinish()
	private void checkFinish() {
		finish = true;
		outer : for(int x = 0;x < COLS;x++) {
			for(int y = 0;y < ROWS;y++) {
				if((matrix[x][y].isOpened() || (matrix[x][y].isBomb() && matrix[x][y].isFlag()) ) == false ) {
					finish = false;
					break outer;
				}
			}
		}
	}
	
	public void reset() {
		for(int x = 0; x<COLS; x++){
			for(int y = 0;y < ROWS; y++){
				matrix[x] [y].reset();
			}
		}
		
		dead = false;
		finish = false;
		
		place_all_bombs();
		set_numeber_of_near_bombs();
	}
	
	
	public void draw(Graphics g){
		for(int x = 0;x < COLS;x++){
			for(int y = 0;y < ROWS;y++){
				matrix[x][y].draw(g);
			}
		}
		
		if(dead){
			g.setColor(Color.RED);
			g.drawString("You're dead!", 10, 30);
		}
		else if(finish){
			g.setColor(Color.GREEN);
			g.drawString("You won!", 10, 30);
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
