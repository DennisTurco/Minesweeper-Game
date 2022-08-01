package game;

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
	
	// CONSTRUCTOR	
	public FrameMineField () { 
		this.setTitle("MineField");
		this.setVisible(true);
		this.setSize(width + getInsets().left + getInsets().right, eight + getInsets().bottom + getInsets().top);
		this.setResizable(false);
		this.setLocationRelativeTo(null); // per far aprire la finestra a centro schermo
	
		screen = new Screen();
		add(screen);
	}
	
	
	//KEYBOARD
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	// MOUSE
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	} 
	
	
	public class Screen extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.RED);
			g.fillRect(0, 0, 50, 50);
		}
	}
	
	
	// GETTER
	public int getWidth() {
		return width;
	}
	public int getEight() {
		return eight;
	}
	
}
