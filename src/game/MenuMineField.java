package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

class MenuMineField extends JFrame implements WindowListener, ActionListener {
	
	private Label lblDisplay;
	
	public MenuMineField () {
		
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
		MenuItem quit = new MenuItem("Quit");
		
		mnuGame.add(new_game);
		mnuOptions.add(quit);
		
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
		
	}
}
