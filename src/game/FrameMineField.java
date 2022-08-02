package game;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Font;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


class FrameMineField extends JFrame implements MouseListener, KeyListener {
	
	public static int width = 400; 
	public static int height = 400;
	
	private Screen screen;
	private WorldMineField world;
	private Font font;
	
	private int insetLeft;
	private int insetTop;
	
	// CONSTRUCTOR	
	public FrameMineField () { 
		this.setTitle("MineField");
		this.setVisible(true);
		insetLeft = getInsets().left;
		insetTop = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop);
		this.setResizable(false);
		this.setLocationRelativeTo(null); // per far aprire la finestra a centro schermo
		
		ImageIcon image = new ImageIcon(".//res//bomb.png"); //crea un'icona
		setIconImage(image.getImage());	//cambia l'icona del frame

		addMouseListener(this);
		addKeyListener(this);

		screen = new Screen();
		add(screen);

		world = new WorldMineField();
	
		font = new Font("SansSerif", 0, 12);
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
			g.setFont(font);
			world.draw(g);
		}
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