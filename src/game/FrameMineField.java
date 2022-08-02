package game;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

@SuppressWarnings("serial")
class FrameMineField extends JFrame implements MouseListener, KeyListener{
	
	public static int width = 400; 
	public static int eight = 400;
	
	private Screen screen;
	private WorldMineField world;
	
	private int insetLeft;
	private int insetTop;
	
	// CONSTRUCTOR	
	public FrameMineField () { 
		this.setTitle("MineField");
		this.setVisible(true);
		this.setSize(width + getInsets().left + getInsets().right, eight + getInsets().bottom + getInsets().top);
		this.setResizable(false);
		this.setLocationRelativeTo(null); // per far aprire la finestra a centro schermo
		
		ImageIcon image = new ImageIcon(".//res//bomb.png"); //crea un'icona
		setIconImage(image.getImage());	//cambia l'icona del frame
	
		screen = new Screen();
		add(screen);
		
		world = new WorldMineField();
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
		if(e.getKeyCode() == KeyEvent.VK_R)
		{
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
		if(e.getButton() == 1) //world.left_click(e.getX() - insetLeft, e.getY() - insetTop);
		if(e.getButton() == 3) //world.right_click(e.getX() - insetLeft, e.getY() - insetTop);
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
			world.draw(g);
		}
	}
	
	
	// GETTER
	public static int getEight() {
		return eight;
	}
		
	public static int getWidth() {
		return width;
	}
	
	
}
