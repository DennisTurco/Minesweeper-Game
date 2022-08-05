package game;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Label;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.MenuItem;


class FrameMineField extends JFrame implements MouseListener, KeyListener, WindowListener, ActionListener{
	
	public static int width = 700; 
	public static int height = 700;
	
	private Screen screen;
	private WorldMineField world;
	private MenuMineField menu;
	private Font font;
	
	private int insetLeft;
	private int insetTop;
	
	private Label lblDisplay;
	
	// CONSTRUCTOR	
	public FrameMineField () { 
		
		world = new WorldMineField();
		menu = new MenuMineField();
	
		addMouseListener(this);
		addKeyListener(this);
		
		screen = new Screen();
		add(screen);
		
		this.setTitle("MineField");
		this.setResizable(false);
		
		this.setLocationRelativeTo(null); // per far aprire la finestra a centro schermo
		this.setVisible(true);
		
		ImageIcon image = new ImageIcon(".//res//bomb.png"); //crea un'icona
		setIconImage(image.getImage());	//cambia l'icona del frame
		
		
		//TODO: farla funzionare nella classe a parte richiamandola
		addWindowListener(this);
		
		// South Label 
		lblDisplay = new Label();
		lblDisplay.setBackground(Color.LIGHT_GRAY);
		add(BorderLayout.SOUTH, lblDisplay);
		
		// Menu Bar
		MenuBar AwtMenuBar = new MenuBar();
		setMenuBar(AwtMenuBar);
		
		// Menus
		Menu mnuGame = new Menu("Game");
		Menu mnuOptions = new Menu("Options");
		AwtMenuBar.add(mnuGame);
		AwtMenuBar.add(mnuOptions);
		
		// Menu Items
		MenuItem new_game = new MenuItem("New Game");
		MenuItem remove_all_flags = new MenuItem("Remove all flags");
		MenuItem quit = new MenuItem("Quit");
		
		mnuGame.add(new_game);
		mnuGame.add(remove_all_flags);
		mnuOptions.add(quit);
		
		// Action Command
		new_game.setActionCommand("New Game");
		remove_all_flags.setActionCommand("Remove All Flags");
		quit.setActionCommand("Quit");
		
		// Action Listener
		new_game.addActionListener(this);
		remove_all_flags.addActionListener(this);
		quit.addActionListener(this);
		
		// Setting Borders
		pack();
		insetLeft = getInsets().left;
		insetTop = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop);
		
	}
	
	
	
	
	public class Screen extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			font = new Font("SansSerif", Font.BOLD, width/world.getCOLS() - width/world.getCOLS()*50/100); // la grandezza dei numeri all'interno delle caselle ridimensionata in base al numero di celle - il 50% del risultato
			g.setFont(font);
			world.draw(g);
		}
	}
	
	
	// https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
	public static void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	
	// GETTER
	public static int getScreenWidth()
	{
		return width;
	}
	
	public static int getScreenHeight()
	{
		return width;
	}
	
	//KEYBOARD
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
			
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_R) {
			world.reset();
			screen.repaint();
		}
	}
	
	
	// MOUSE
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1) world.left_click(e.getX() - insetLeft, e.getY() - insetTop);
		if(e.getButton() == 3) world.right_click(e.getX() - insetLeft, e.getY() - insetTop);
		screen.repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		//world.highlight(e.getX() - insetLeft, e.getY() - insetTop);
		//screen.repaint();
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
			
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	} 
	
	@Override
	public void windowOpened(WindowEvent e) {
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {	
		System.out.println("entrato");
		world.reset();
	}
	
}