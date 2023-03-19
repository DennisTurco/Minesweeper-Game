package game;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.*;

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
	private static int COLS;
	private static int ROWS;
	private static int N_BOMBS; 
	private static int N_FLAGS;
	private static boolean dead;
	private static boolean finish;
	private static boolean started;
	private static TileMinesweeper[][] matrix;
	private static TimerMinesweeper timer = new TimerMinesweeper();
	private static String[] list;
	
	public static String []DIFFICULTY = {"difficultyEasy", "difficultyNormal", "difficultyHard"};
	private static String current_difficulty;
	
	private static FrameMinesweeper frame;
	
	// scale images based on screen size
	private BufferedImage bomb_img;
	private BufferedImage bomb_no_face_img;
	private BufferedImage flag_img;
	private BufferedImage pressed_img;
	private BufferedImage pressed2_img;
	private BufferedImage normal_img;
	private BufferedImage normal2_img;
	private BufferedImage error_img;
	
	//TODO: add the texture of a flower to be inserted when you win
	//TODO: add delay between the images
	//TODO: add hover on selected boxes
	
	//CONSTRUCTOR
	public WorldMinesweeper(String difficulty) {
		
		// set the difficulty
		if (difficulty.equals(DIFFICULTY[0])) {
			current_difficulty = DIFFICULTY[0];
			COLS = 11;
			ROWS = 11;
		}
		if (difficulty.equals(DIFFICULTY[1])) {
			current_difficulty = DIFFICULTY[1];
			COLS = 15;
			ROWS = 15;
		} 
		if (difficulty.equals(DIFFICULTY[2])) {
			current_difficulty = DIFFICULTY[2];
			COLS = 21;
			ROWS = 21;
		} 
		N_BOMBS = COLS*ROWS*16/100; // the amount of bombs is given by about 16% of the total number of boxes (COLS*ROWS)
		N_FLAGS = N_BOMBS;
		
		scaleImages();
		
		matrix = new TileMinesweeper[ROWS][COLS];
		
		// destroy the old frame
		if (frame != null) {
			frame.setVisible(false); 
			frame.dispose(); //Destroy the JFrame object
		}
		
		frame = new FrameMinesweeper();
		
		// build every cell
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
		
		reset(); //reset the world and create a new game
	}
	
	private void scaleImages() {
		bomb_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/bomb_face.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		bomb_no_face_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/bomb.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		flag_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/flag.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		pressed_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_brown_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		pressed2_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_brown2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		normal_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_green_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		normal2_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/tile_green2_normal.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
		error_img = ImageLoader_Minesweeper.scale(ImageLoader_Minesweeper.loadImage("res/error.png"), TileMinesweeper.getWidth(), TileMinesweeper.getHeight());
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
	
	public static void showNumberOfFlag() {
		int count = 0;
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && matrix[i][j].isOpened()==false) count++;
			}
		}
		
		N_FLAGS = N_BOMBS-count;
		frame.setFlagsNumber(N_FLAGS);
	}
	
	public static void showWrongFlags() {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isFlag() && !(matrix[i][j].isBomb() || matrix[i][j].isBombFace())) {
					matrix[i][j].setError(true);
					
				}
			}
			
		}
		
	}

	
	public static void left_click(int x, int y) {
		int x_axis = x/TileMinesweeper.getWidth();
		int y_axis = y/TileMinesweeper.getHeight();
		
		// case of the first click of the game start
		if (started == false) {
			while(!(matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false)) {
				reset();
			}
		}
		
		if (dead == false && finish == false) {	
			started = true;
			
			// start the timer
			if (timer.isTimeRunning() == false) { 
				timer.startTimer();  
			}
			
			if (matrix[x_axis][y_axis].isFlag()) return;
			else if (matrix[x_axis][y_axis].isOpened()) return;
			else if (matrix[x_axis][y_axis].isBomb()) {
				dead = true;
				
				showAllBombs();
				showWrongFlags();
				
				matrix[x_axis][y_axis].setBomb(false); // remove the default image of the bomb
				matrix[x_axis][y_axis].setBombFace(true); // add the bomb with the face in the current position
				
				// sound
				try {
					openSound(".//res//buttonEffect.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			else if (matrix[x_axis][y_axis].getAmountOfNearBombs() == 0 && matrix[x_axis][y_axis].isBomb() == false) open(x_axis, y_axis);
			
			matrix[x_axis][y_axis].setOpened(true);
			
			if (!dead) {
				// sound
				try {
					String []choose = {"sound_effect_open1.wav", "sound_effect_open2.wav", "sound_effect_open3.wav", "sound_effect_open4.wav"}; 
					Random random = new Random(); 
					int index = random.nextInt(4-1) + 1;
					openSound(".//res//"+choose[index]);
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			
			checkFinish();
			
			if ((finish == true || dead == true) && timer.isTimeRunning() == true ) {
				timer.stopTimer(); // delete the timer
				timer = new TimerMinesweeper(); // create a new Timer object 
			}
			
			if (dead == false && finish == true) {
			    try {
				    String result = JOptionPane.showInputDialog(null, "Enter your name:", "Congratulations!! Seconds Passed = " + timer.getTimer(), JOptionPane.INFORMATION_MESSAGE); // popup message
				    if (result.length() > 0) newScoreScoreboard(result); // pass the winner’s name in the scoreboard
					OpenScoreboard(result, current_difficulty);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				} 
			   
			}
		}

	}
	
	public static void right_click(int x, int y) {
		if(dead == false && finish == false){
			
			if (timer.isTimeRunning() == false) { // start the timer
				timer.startTimer();
			}
			
			int x_axis = x/TileMinesweeper.getWidth(); // get the correct position according to the screen
			int y_axis = y/TileMinesweeper.getHeight();
			matrix[x_axis][y_axis].placeFlag(); // place a flag in the correct position
			
			showNumberOfFlag(); // recalculates the number of flags
			
			// sound
			if (!matrix[x_axis][y_axis].isOpened()) {
				try {
					openSound(".//res//sound_effect_flag.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
					e.printStackTrace();
					System.err.println("Exception --> " + e);
				}
			}
			
		}
		
	}
	
	public static void openSound(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (frame.isSoundEffectActive() == false) return; // case of leaving
		
		File file = new File(path);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
		Clip clip = AudioSystem.getClip();
		clip.open(audioStream);
		clip.start();
	}
	
	
	private static void checkFinish() {
		finish = true; 
		for(int i = 0; i<COLS; i++) {
			for(int j=0; j<ROWS; j++) {
				if (matrix[i][j].isBomb() && matrix[i][j].isOpened()) {
					dead = true;
					return;
				} 
				if (matrix[i][j].isBomb() == false && matrix[i][j].isOpened() == false) { // if I find a box not open bomb then it is not finished
					finish = false;
					return;
				}
			}
		}
		
	}
	
	
	//TODO: fix
	private void winLayout() {
		// remove the flags
		removeAllFlags();
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.err.println("Exception --> " + e);
		}
		
		// add flowers
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if (matrix[i][j].isBomb()) {
					try {
						TimeUnit.MILLISECONDS.sleep(100);
						matrix[i][j].placeFlower();
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.err.println("Exception --> " + e);
					}
				}
			}
		}
	}
	
	public static void reset() {
		
		// clear the playing field
		for(int i=0; i<COLS; i++){
			for(int j=0; j<ROWS; j++){
				matrix[i][j].reset();
			}
		}
		
		// reset the booleans
		dead = false;
		finish = false;
		started = false;
		
		// replace bombs and numbers next to each other
		place_all_bombs();
		set_numeber_of_near_bombs();
		
		// reset the values in the toolbar
		frame.setFlagsNumber(N_BOMBS);
		frame.setTilesNumber(COLS*ROWS);
		frame.setTimer(0);	
		
		// restart timer
		timer.setTimer(0);
		if (timer.isTimeRunning() == true) {
			timer.stopTimer(); // delete the timer
			timer = new TimerMinesweeper(); //create a new Timer object 
		} 
	}
	
	
	public static void draw(Graphics g){

		// the writing will be with a size of 10% of the game window
		Font font = new Font("SansSerif", 0, frame.getScreenWidth()*10/100); 
		
		Rectangle rect = new Rectangle(frame.width, frame.height);
		for(int x = 0;x < COLS;x++){
			for(int y = 0;y < ROWS;y++){
				matrix[x][y].draw(g);
			}
		}
		
		if(dead){
			g.setColor(Color.RED);
			frame.drawCenteredString(g, "Game Over!", rect, font);
		}
		else if(finish){
			g.setColor(Color.GREEN);
			frame.drawCenteredString(g, "You Won!!", rect, font);
		}
	}
	
	public static void OpenRules () throws Exception {	
		ImageIcon icon = new ImageIcon(".//res//info.png");
		JOptionPane.showMessageDialog(null, 
				"Minesweeper rules are very simple: \r\n"
				+ "			- The board is divided into cells, with mines randomly distributed. \r\n"
				+ "			- To win, you need to open all the cells. \r\n"
				+ "			- The number on a cell shows the number of mines adjacent to it. \r\n"
				+ "			- Using this information, you can determine cells that are safe, and cells that contain mines. \r\n"
				+ "			- Cells suspected of being mines can be marked with a flag using the right mouse button.",
				"How To Play",
				JOptionPane.PLAIN_MESSAGE, icon); // popup message

	}
	
	public static void OpenCredits() {
		ImageIcon icon = new ImageIcon(".//res//author_logo.png");
		JOptionPane.showMessageDialog(null, 
				"<html><u>2022 © Dennis Turco</u></html>\r\n"
				+ "<html><i>Author</i>: Dennis Turco</html>\r\n"
				+ "<html><i>GitHub</i>: <a href='https://github.com/DennisTurco'>https://github.com/DennisTurco </a></html>\r\n"
				+ "<html><i>Web Site</i>: <a href='https://dennisturco.github.io/'>https://dennisturco.github.io/ </a></html>",
				"Credits",
				JOptionPane.PLAIN_MESSAGE, icon); // popup message
	}

	
	// ######################## Scoreboard ########################
	public static void OpenScoreboard(String player_name, String difficulty) throws Exception {
		getScoreboard(difficulty);
		
		
		// I use the JLabel instead of simple String because I want to color the string just added
		String new_score = player_name + " -->  Seconds: " + timer.getTimer();
		JPanel pnl = new JPanel();
		pnl.setBounds(61, 11, 81, 140);
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS)); // VerticalLayout
		JLabel []labels = new JLabel[list.length];
			
		for (int i=0; i<list.length; i++) {
			if (list[i] != null) {
				
				labels[i] = new JLabel();
				
				if (list[i].equals(new_score)) { // color only the one just inserted (if and only if it enters in top) 
					new_score = (i+1) + ". " + new_score + "\n";
					labels[i].setText(new_score);
					labels[i].setForeground(new Color(50,205,50));
				}
				
				else labels[i].setText((i+1) + ". " + list[i]);
				
				pnl.add(labels[i]);
			}
		}
		
		ImageIcon icon = new ImageIcon(".//res//trophy.png");		
		JOptionPane.showMessageDialog(null, pnl, "Scoreboard", JOptionPane.PLAIN_MESSAGE, icon); // popup message
		
	}
	
	private static void newScoreScoreboard(String name) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(".//res//scoreboard" + "_" + current_difficulty, true)); //true = append
        bw.write("" + name + " -->  Seconds: " + timer.getTimer() + "\n");
        bw.close();
        getScoreboard(current_difficulty); // get the scoreboard
	}
	
	private static void getScoreboard(String difficulty) {
		int DIM_MAX = 11;
		list = new String[DIM_MAX]; // the ranking is a top 10
		
		// read the lines
		try {
			BufferedReader br = new BufferedReader(new FileReader(".//res//scoreboard" + "_" + difficulty));
			
			for (int i=0; i<DIM_MAX; i++) {
				if ((list[i] = br.readLine()) != null);
			}
			
			br.close();
		} catch (IOException e) {
			System.err.println("Exception --> " + e);
		}
		
		// sort
		list = sortScoreboard(list, DIM_MAX);
		
		// write the new values ordered
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(".//res//scoreboard" + "_" + difficulty, false)); // false = append
			for (int i=0; i<DIM_MAX-1; i++) {
	        	if (list[i] != null) bw.write("" + list[i] + "\n");
	        }
	        bw.close();
		} catch (IOException e) {
			System.err.println("Exception --> " + e);
		} 		
		
	}
	
	private static String[] sortScoreboard(String[] list, int DIM_MAX) {
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
		
		return list;
	}
	// ########################
	
	
	private static void open(int x, int y) {
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
	
	public static void showAllBombs () {
		for (int i=0; i<ROWS; i++) {
			for (int j=0; j<COLS; j++) {
				if ((matrix[i][j].isBomb() || matrix[i][j].isBombFace()) && matrix[i][j].isFlag() == false) matrix[i][j].setOpened(true);
			}
		}
	}
	
	public static void removeAllFlags() {
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

				// I’m not on the bomb
				if (!matrix[i][j].isBomb()) { 
					if (j+1 < ROWS) if (matrix[i][j+1].isBomb()) number_of_near_bombs++; // check on the right
					if (j-1 >= 0) 	if (matrix[i][j-1].isBomb()) number_of_near_bombs++; // check on the left
					if (i+1 < COLS) if (matrix[i+1][j].isBomb()) number_of_near_bombs++; // check on the bottom
					if (i-1 >= 0) 	if (matrix[i-1][j].isBomb()) number_of_near_bombs++; // check on the top
					if (i-1 >= 0 && j+1 < ROWS) 	if (matrix[i-1][j+1].isBomb()) number_of_near_bombs++; // check on the top right
					if (i-1 >= 0 && j-1 >= 0) 		if (matrix[i-1][j-1].isBomb()) number_of_near_bombs++; // check on the top left
					if (i+1 < COLS && j+1 < ROWS) 	if (matrix[i+1][j+1].isBomb()) number_of_near_bombs++; // check on the bottom right
					if (i+1 < COLS && j-1 >= 0) 	if (matrix[i+1][j-1].isBomb()) number_of_near_bombs++; // check on the bottom left
					
					matrix[i][j].setAmountOfNearBombs(number_of_near_bombs);
					number_of_near_bombs = 0;
				}
				
			}
			
		}	
	}
	
}