package game;

import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

class WorldMinesweeper {
	private static int COLS = 15;
	private static int ROWS = 15;
	private static int N_BOMBS = COLS*ROWS*16/100; // la quantità di bombe è data circa dal 16% del numero totale di caselle (COLS*ROWS)
	private static int N_FLAGS = N_BOMBS;
	private static boolean dead;
	private static boolean finish;
	private static boolean started;
	private static TileMinesweeper[][] matrix;
	private static TimerMinesweeper timer = new TimerMinesweeper();
	
	// scalo le immagini in base alla dimensione dello schermo
	private BufferedImage bomb_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/bomb_face.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage bomb_no_face_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/bomb.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage flag_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/flag.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage pressed_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_brown_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage pressed2_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_brown2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage normal_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_green_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage normal2_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_green2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	private BufferedImage error_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/error.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
	
	//TODO: aggiungere la texture di un fiore da inserire quando si vince
	//TODO: aggiungere i suoni e migliorarli
	//TODO: aggiungere la possibilità di cambiare difficoltà
	//TODO: aggiungere delay tra le immagini
	//TODO: aggiungere hover sulle caselle selezionate
	
	//CONSTRUCTOR
	public WorldMinesweeper() {
		
		matrix = new TileMinesweeper[ROWS][COLS];
		
		// costruisco ogni cella
		boolean tile_switch = false;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (tile_switch == false) {
					matrix[i][j] = new TileMinesweeper(i, j, normal_img, bomb_no_face_img, bomb_img, pressed_img, flag_img, error_img);
					tile_switch = true;
				}
				else {
					matrix[i][j] = new TileMinesweeper(i, j, normal2_img, bomb_no_face_img, bomb_img, pressed2_img, flag_img, error_img);
					tile_switch = false;
				}
				
			}
			
		}
		
		reset(); //resetto il mondo e creo una nuova partita
		
	}
	
	private static void place_all_bombs() {
		for (int i=0; i<N_BOMBS; i++) { 
			place_bomb();
		}
	}
	
	private static void place_bomb() {
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
		FrameMinesweeper.setFlagsNumber(N_FLAGS);
	}
	
	public void showWrongFlags() {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && !(matrix[i][j].isBomb() || matrix[i][j].isBombFace())) {
					matrix[i][j].setError(true);
					
				}
			}
			
		}
		
	}

	
	public void left_click(int x, int y) {
		
		
		int x_axis = x/TileMinesweeper.getWidth();
		int y_axis = y/TileMinesweeper.getHeight();
		
		// caso del primo click di inizio partita
		if (started == false) {
			while(!(matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false)) {
				reset();
			}
		}
		
		
		if (dead == false && finish == false) {
			
			started = true;
			
			if (timer.isTimeRunning() == false) { //avvio il timer
				timer.startTimer();  
			}
			
			if (matrix[x_axis][y_axis].isFlag()) return;
			else if (matrix[x_axis][y_axis].isOpened()) return;
			else if (matrix[x_axis][y_axis].isBomb()) {
				dead = true;
				
				showAllBombs();
				showWrongFlags();
				
				matrix[x_axis][y_axis].setBomb(false); // tolgo l'immagine di default della bomba
				matrix[x_axis][y_axis].setBombFace(true); // aggiungo la bomba con la faccia nella posizione attuale
				
				// sound
				try {
					openSound(".//res//buttonEffect.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
			}
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
			
			if ((finish == true || dead == true) && timer.isTimeRunning() == true ) {
				timer.stopTimer(); // elimino il timer
				timer = new TimerMinesweeper(); //creo un nuovo oggetto Timer 
			}
			
			if (dead == false && finish == true) {
			    String result = JOptionPane.showInputDialog(null, "Enter your name:", "Congratulations!! Seconds Passed = " + timer.getTimer(), JOptionPane.INFORMATION_MESSAGE); //messaggio popup
			    try {
			    	System.out.println(result.length());
					if (result.length() > 0) newScoreScoreboard(result); // passo il nome del vincitore nella scoreboard
				} catch (IOException e) {
					e.printStackTrace();
				} 
			   
			}
		}

	}
	
	public void right_click(int x, int y) {
		if(dead == false && finish == false){
			
			if (timer.isTimeRunning() == false) { //avvio il timer
				timer.startTimer();
			}
			
			int x_axis = x/TileMinesweeper.getWidth(); // ottengo la corretta posizione in base allo schermo
			int y_axis = y/TileMinesweeper.getHeight();
			matrix[x_axis][y_axis].placeFlag(); //piazzo una flag nella posizione corretta
			
			showNumberOfFlag(); // ricalcola il numero di flag
			
			// sound
			try {
				openSound(".//res//placeflag.wav");
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void openSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (FrameMinesweeper.isSoundEffectActive() == false) return; // caso di uscita
		
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
	
	public static void reset() {
		
		// azzero il campo da gioco
		for(int i=0; i<COLS; i++){
			for(int j=0; j<ROWS; j++){
				matrix[i][j].reset();
			}
		}
		
		// azzero i booleani
		dead = false;
		finish = false;
		started = false;
		
		// ripiazzo le bombe e i numeri affianco
		place_all_bombs();
		set_numeber_of_near_bombs();
		
		// resetto i valori nella ToolBar
		FrameMinesweeper.setFlagsNumber(N_BOMBS);
		FrameMinesweeper.setTilesNumber(COLS*ROWS);
		FrameMinesweeper.setTimer(0);	
		
		// restart timer
		timer.setTimer(0);
		if (timer.isTimeRunning() == true) {
			timer.stopTimer(); // elimino il timer
			timer = new TimerMinesweeper(); //creo un nuovo oggetto Timer 
		} 
	}
	
	
	public void draw(Graphics g){
		Font font = new Font("SansSerif", 0, FrameMinesweeper.getScreenWidth()*10/100); // la scritta sarà con una grandezza del 10% della finestra di gioco
		Rectangle rect = new Rectangle(FrameMinesweeper.width, FrameMinesweeper.height);
		for(int x = 0;x < COLS;x++){
			for(int y = 0;y < ROWS;y++){
				matrix[x][y].draw(g);
			}
		}
		
		if(dead){
			g.setColor(Color.RED);
			FrameMinesweeper.drawCenteredString(g, "Game Over!", rect, font);
		}
		else if(finish){
			g.setColor(Color.GREEN);
			FrameMinesweeper.drawCenteredString(g, "You Won!!", rect, font);
		}
	}
	
	public void OpenRules () throws Exception {
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("notepad.exe .//res//rules");
	}
	
	// ######################## Scoreboard ########################
	public void OpenScoreboard() throws Exception {
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("notepad.exe .//res//scoreboard");
	}
	
	private void newScoreScoreboard(String name) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(".//res//scoreboard", true)); //true = append
        bw.write("" + name + " -->  Seconds: " + timer.getTimer() + "\n");
        bw.close();
        
        SortScoreboard(); //ordino la scoreboard
	}
	
	private void SortScoreboard() {
		//leggo le righe
		int DIM_MAX = 11;
		String []list = new String[DIM_MAX]; //la classifica è una top 10
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(".//res//scoreboard"));
			
			for (int i=0; i<DIM_MAX; i++) {
				if ((list[i] = br.readLine()) != null) ;
			}
			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//ordino
		float value1 = 0, value2 = 0;
		for (int i=0; i<DIM_MAX; i++) {
			if (list[i] != null) {
				for (int c=0; c<list[i].length(); c++) {
					if (list[i].charAt(c) == ':') {
						value1 = Float.parseFloat(list[i].substring(c+2, list[i].length()));
						break;
					}
				}
			}
			
			for (int j=i+1; j<DIM_MAX; j++) {
				if (list[j] != null) {
					for (int c=0; c<list[j].length(); c++) {
						if (list[j].charAt(c) == ':') {
							value2 = Float.parseFloat(list[j].substring(c+2, list[j].length()));
							break;
						}
					}
					
					if (value1 >= value2) {
						String temp = list[i];
						list[i] = list[j];
						list[j] = temp;
					}
				}
				
			}
		}
		
		// scrivo i nuovi valori ordinati
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(".//res//scoreboard", false)); //false = append
			for (int i=0; i<DIM_MAX-1; i++) {
	        	if (list[i] != null) bw.write("" + list[i] + "\n");
	        }
	        bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
		
		
	}
	// ########################
	
	
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
	
	
	// GETTER
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
	
	// SETTER
	public static void setCOLS(int value) {
		COLS = value;
		reset();
	}
	
	public static void setROWS(int value) {
		ROWS = value;
		reset();
	}
	
	
	
	private static void set_numeber_of_near_bombs() {
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
	
}