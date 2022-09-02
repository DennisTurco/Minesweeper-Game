package game;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

import javax.swing.*;


@SuppressWarnings("serial")
class FrameMinesweeper extends JFrame implements MouseListener, WindowListener, ActionListener{
	
	public static int width = 600; 
	public static int height = 600;
	
	private static Screen screen;
	private WorldMinesweeper world;
	private Font font;
	
	private JMenuBar menu_bar;
	private JToolBar tool_bar;
	
	private static JButton flags_number;
	private static JButton tiles_number;
	private static JButton time_number;
	private static JCheckBoxMenuItem sounds;
	
	private int insetLeft;
	private int insetTop;
	
	// CONSTRUCTOR	
	public FrameMinesweeper () { 
		
		addMouseListener(this);
		
		screen = new Screen();
		this.setLayout(new BorderLayout());
		add(screen, BorderLayout.CENTER);
		
		// ------ ToolBar
		tool_bar = new JToolBar();
		tool_bar.setFloatable(false);
		this.add(tool_bar, BorderLayout.NORTH);
		
		JPanel panel = new JPanel(); // mi serve per centrare gli elementi
		
		Icon icon1 = new ImageIcon("res/tile_green_normal.png");
		Icon icon2 = new ImageIcon("res/flag.png");
		Icon icon3 = new ImageIcon("res/clock.png");
		
		tiles_number = new JButton("Tiles = ");
		flags_number = new JButton("Flags = ");
		time_number = new JButton("Time = ");
		
		tiles_number.setIcon(icon1);
		flags_number.setIcon(icon2);
		time_number.setIcon(icon3);
		
		tiles_number.setBorderPainted(false);
		flags_number.setBorderPainted(false);
		time_number.setBorderPainted(false);
		
		tiles_number.setFocusPainted(false);
		flags_number.setFocusPainted(false);
		time_number.setFocusPainted(false);
		
		tiles_number.setContentAreaFilled(false);
		flags_number.setContentAreaFilled(false);
		time_number.setContentAreaFilled(false);
		
		panel.add(tiles_number);
		panel.add(time_number);
		panel.add(flags_number);
		tool_bar.add(panel);
		// ------
		
		// Author
		/*JPanel panel_author = new JPanel();
		JLabel author = new JLabel("© Dennis Turco 2022");
		author.setForeground(Color.WHITE);	
		panel_author.setOpaque(false);
		panel_author.add(author);
		screen.add(panel_author);*/
		
		//TODO: farla funzionare nella classe a parte richiamandola
		addWindowListener(this);
		
		// Menu Bar
		menu_bar = new JMenuBar();
		setJMenuBar(menu_bar);
		
		// Menus
		JMenu mnuGame = new JMenu("Game");
		JMenu mnuOptions = new JMenu("Options");
		JMenu submnuNewGame = new JMenu("New game");  //sub menu
		menu_bar.add(mnuGame);
		menu_bar.add(mnuOptions);
		
		// Menu Items
		JMenuItem restart = new JMenuItem("Restart");
		JMenuItem remove_all_flags = new JMenuItem("Remove all flags");
		JMenuItem scoreboard = new JMenuItem("Scoreboard");
		JMenuItem rules = new JMenuItem("How To Play");
		JMenuItem share = new JMenuItem("Share");
		JMenuItem quit = new JMenuItem("Quit");
		JMenuItem easy = new JMenuItem("Easy");
		JMenuItem normal = new JMenuItem("Normal");
		JMenuItem hard = new JMenuItem("Hard");
		sounds = new JCheckBoxMenuItem("Sounds Effect"); 
		mnuGame.add(restart);
		mnuGame.add(submnuNewGame);
		mnuGame.add(remove_all_flags);
		mnuGame.add(scoreboard);
		mnuOptions.add(sounds);
		mnuOptions.add(rules);
		mnuOptions.add(share);
		mnuOptions.add(quit);
		submnuNewGame.add(easy);
		submnuNewGame.add(normal);
		submnuNewGame.add(hard);
		
		sounds.setSelected(true);
		
		// Action Command
		restart.setActionCommand("Restart");
		remove_all_flags.setActionCommand("Remove All Flags");
		scoreboard.setActionCommand("Scoreboard");
		rules.setActionCommand("Rules");
		rules.setActionCommand("Share");
		quit.setActionCommand("Quit");
		easy.setActionCommand("Easy");
		normal.setActionCommand("Normal");
		hard.setActionCommand("Hard");
		sounds.setActionCommand("Sounds Effect");
		
		// Action Listener
		restart.addActionListener(this);
		remove_all_flags.addActionListener(this);
		scoreboard.addActionListener(this);
		rules.addActionListener(this);
		share.addActionListener(this);
		quit.addActionListener(this);
		easy.addActionListener(this);
		normal.addActionListener(this);
		hard.addActionListener(this);
		sounds.addActionListener(this);
		
		// Acceleration
		restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK)); // ctrl+r
		remove_all_flags.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK)); // ctrl+f
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)); // alt+f4
		
		world = new WorldMinesweeper();
		
		this.setTitle("Minesweeper");
		this.setResizable(false);
		this.setLocation(650, 200); // per far aprire la finestra a centro schermo
		this.setVisible(true);
		
		ImageIcon image = new ImageIcon(".//res//bomb.png"); //crea un'icona
		setIconImage(image.getImage());	//cambia l'icona del frame
		
		// Setting Borders
		setBorders();

	}
	
	public class Screen extends JPanel {
		@Override
		public void paintComponent(Graphics g) {
			font = new Font("SansSerif", Font.BOLD, width/world.getCOLS() - width/world.getCOLS()*50/100); // la grandezza dei numeri all'interno delle caselle ridimensionata in base al numero di celle - il 50% del risultato
			g.setFont(font);
			world.draw(g);
		}
	}
	
	private void setBorders() {
		pack();
		insetLeft = getInsets().left;
		insetTop = getInsets().top;
		setSize(width + insetLeft + getInsets().right, height + getInsets().bottom + insetTop + menu_bar.getHeight() + tool_bar.getHeight());
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
	public static int getScreenWidth(){
		return width;
	}
	
	public static int getScreenHeight(){
		return width;
	}
	
	public static boolean isSoundEffectActive() {
		return sounds.isSelected();
	}
	
	
	// SETTER
	public static void setFlagsNumber(int value) {
		flags_number.setText("Flags = " + value);
	}
	
	public static void setTilesNumber(int value) {
		tiles_number.setText("Tiles = " + value);
	} 
	
	public static void setTimer(float value) {
		time_number.setText("Time = " + value);
	}
	
	// MOUSE
	@Override
	public void mousePressed(MouseEvent e) {
		
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1) world.left_click(e.getX() - insetLeft, e.getY() - insetTop - menu_bar.getHeight() - tool_bar.getHeight());
		if(e.getButton() == 3) world.right_click(e.getX() - insetLeft, e.getY() - insetTop - menu_bar.getHeight() - tool_bar.getHeight());
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
		String command = e.getActionCommand();
		
		if(command.equals("Restart")) {
			world.reset();
			screen.repaint();
		}
		/*else if (command.equals("Easy")) { 
			world = new WorldMinesweeper(7, 7); 
			world.draw(getGraphics());
		}	//TODO: add
		
		else if (command.equals("Normal")) world = new WorldMinesweeper(15, 15); //TODO: add
		else if (command.equals("Hard")) world = new WorldMinesweeper(25, 25); //TODO: add*/
		else if (command.equals("Remove All Flags")) {
			world.removeAllFlags();
			screen.repaint();
		}
		else if (command.equals("Quit")) System.exit(EXIT_ON_CLOSE);
		else if (command.equals("Share")) {
			//messaggio pop-up
			JOptionPane.showMessageDialog(null, "Share link copied to clipboard!");
	        
			//copio nella clipboard il link
	        String testString = "https://github.com/DennisTurco/Minesweeper-Game";
	        StringSelection stringSelectionObj = new StringSelection(testString);
	        Clipboard clipboardObj = Toolkit.getDefaultToolkit().getSystemClipboard();
	        clipboardObj.setContents(stringSelectionObj, null);
		}
		
		else if (command.equals("Rules")) {
			try {
				world.OpenRules();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		else if (command.equals("Scoreboard"))
			try {
				world.OpenScoreboard();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		else;
	}
	
}