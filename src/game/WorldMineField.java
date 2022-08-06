package game;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

class WorldMineField {
	private static int COLS = 15;
	private static int ROWS = 15;
	private static int N_BOMBS = 1;//COLS*ROWS*16/100; // la quantità di bombe è data circa dal 16% del numero totale di caselle (COLS*ROWS)
	private static int N_FLAGS = N_BOMBS;
	private static int SCORE = 0;
	private static int TIMER = 0;
	private static boolean dead;
	private static boolean finish;
	private static TileMineField[][] matrix;
	
	// scalo le immagini in base alla dimensione dello schermo
	
	private BufferedImage bomb_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/bomb_face.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage bomb_no_face_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/bomb.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage flag_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/flag.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage pressed_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/tile_brown_normal.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage pressed2_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/tile_brown2_normal.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage normal_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/tile_green_normal.png"), TileMineField.getWidth(), TileMineField.getHeight());
	private BufferedImage normal2_img = ImageLoader_MineField.scale(ImageLoader_MineField.loadImage("res/tile_green2_normal.png"), TileMineField.getWidth(), TileMineField.getHeight());

	//TODO: aggiungere la texture di un fiore da inserire quando si vince
	//TODO: aggiungere i suoni e migliorarli
	//TODO: aggiungere il tempo trascorso
	//TODO: aggiungere la possibilità di cambiare difficoltà
	//TODO: aggiungere delay tra le immagini
	//TODO: aggiungere hover sulle caselle selezionate
	
	//CONSTRUCTOR
	public WorldMineField() {
		
		System.out.println("COLS*ROWS = " + COLS*ROWS);
		System.out.println("N_BOMBS = " + N_BOMBS);
		
		matrix = new TileMineField[ROWS][COLS];
		
		// costruisco ogni cella
		boolean tile_switch = false;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (tile_switch == false) {
					matrix[i][j] = new TileMineField(i, j, normal_img, bomb_no_face_img, bomb_img, pressed_img, flag_img);
					tile_switch = true;
				}
				else {
					matrix[i][j] = new TileMineField(i, j, normal2_img, bomb_no_face_img, bomb_img, pressed2_img, flag_img);
					tile_switch = false;
				}
				
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
	
	public void showNumberOfFlag() {
		int count = 0;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && matrix[i][j].isOpened()==false) count++;
			}
		}
		
		N_FLAGS = N_BOMBS-count;
		FrameMineField.setFlagsNumber(N_FLAGS);
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
			else if (matrix[x_axis][y_axis].isBomb()) {
				dead = true;
				
				showAllBombs();
				//TODO: ahhiungere anche removeWrongFlags
				
				matrix[x_axis][y_axis].setBomb(false); // tolgo l'immagine di default della bomba
				matrix[x_axis][y_axis].setBombFace(true); // aggiungo la bomba con la faccia nella posizione attuale
				
				// sound
				try {
					openSound(".//res//buttonEffect.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
			}
			else if (matrix[x_axis][y_axis].isFlag()) return;
			else if (matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false) open(x_axis, y_axis);
			
			matrix[x_axis][y_axis].setOpened(true);
			
			if (!dead) {
				// sound
				try {
					openSound(".//res//placeflag.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
			}
			
			
			checkFinish();
			score();
			
			if (dead == false && finish == true) {
				System.out.println("Score = " + SCORE);
				JFrame frame = new JFrame();
			    Object result = JOptionPane.showInputDialog(frame, "Enter your name:");
			    System.out.println(result); 
			   
			}
		}
	}
	
	public void right_click(int x, int y) {
		if(dead == false && finish == false){
			int x_axis = x/TileMineField.getWidth(); // ottengo la corretta posizione in base allo schermo
			int y_axis = y/TileMineField.getHeight();
			matrix[x_axis][y_axis].placeFlag(); //piazzo una flag nella posizione corretta
			
			showNumberOfFlag(); // ricalcola il numero di flag
			
			// sound
			try {
				openSound(".//res//placeflag.wav");
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
			
			checkFinish(); // controllo se il gioco è terminato
		}
		
	}
	
	public void openSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (FrameMineField.isSoundEffectActive() == false) return; // caso di uscita
		
		File file = new File(path);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();
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
		
		// resetto i valori nella ToolBar
		FrameMineField.setFlagsNumber(N_BOMBS);
		FrameMineField.setTilesNumber(COLS*ROWS);
		FrameMineField.setScore(0);
			
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
			FrameMineField.drawCenteredString(g, "Game Over!", rect, font);
			//FrameMineField.setLabelPanel(); 
		}
		else if(finish){
			g.setColor(Color.GREEN);
			FrameMineField.drawCenteredString(g, "You Won!!", rect, font);
			//FrameMineField.setLabelPanel(); 
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
	
	public void showAllBombs () {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if ((matrix[i][j].isBomb() || matrix[i][j].isBombFace()) && matrix[i][j].isFlag() == false) matrix[i][j].setOpened(true);
			}
		}
	}
	
	public void removeAllFlags() {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag()) matrix[i][j].placeFlag();
			}
		}
	}
	
	//TODO: aggiungere
	public void score() {
		int score = 0;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isOpened() == true && matrix[i][j].isBomb() == false && matrix[i][j].isBombFace() == false) score++;
			}
		}
		
		if (dead == false && finish == true) score += N_BOMBS; // caso in cui il giocatore ha vinto
		
		SCORE = score;
		FrameMineField.setScore(SCORE);
	}
	
	
	
	public static int getCOLS() {
		return COLS;
	}
	
	public static int getROWS() {
		return ROWS;
	} 
	
	public static int getN_BOMBS() {
		return N_BOMBS;
	} 
	
	public static int getN_FLAGS() {
		return N_FLAGS;
	} 
	
}