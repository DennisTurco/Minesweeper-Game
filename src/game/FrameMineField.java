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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class FrameMineField extends JFrame implements MouseListener, KeyListener {
	
	public static int width = 700; 
	public static int height = 700;
	
	private Screen screen;
	private WorldMineField world;
	private Font font;
	
	private int insetLeft;
	private int insetTop;
	
	// CONSTRUCTOR	
	public FrameMineField () { 
		
		this.setTitle("MineField");
		
		world = new WorldMineField();
		
		this.setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addMouseListener(this);
		addKeyListener(this);
		
		screen = new Screen();
		add(screen);
		
		
		pack();
		insetLeft = getInsets().left;
		insetTop = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop);
		this.setLocationRelativeTo(null); // per far aprire la finestra a centro schermo
		this.setVisible(true);
		
		ImageIcon image = new ImageIcon(".//res//bomb.png"); //crea un'icona
		setIconImage(image.getImage());	//cambia l'icona del frame

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
	
}