package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class TileMinesweeper {
	private BufferedImage normal;
	private BufferedImage openedImage;
	private BufferedImage flagImage;
	private BufferedImage bombImage;
	private BufferedImage bombImage_face;
	
	private int x;
	private int y;
	private boolean bomb;
	private boolean bomb_face;
	private boolean opened;
	private boolean flag;
	private int amountOfNearBombs;
	
	Color color1 = new Color (25, 118, 210);	// blu
	Color color2 = new Color (56, 142, 60); 	// verde
	Color color3 = new Color (211, 47, 47); 	// rosso
 	Color color4 = new Color (123, 31, 162); 	// viola
	Color color5 = new Color (245, 231, 0);		// giallo
	Color color6 = new Color (181, 30, 142);	// ...
	Color color7 = new Color (0, 80, 107);		// ...
	Color color8 = new Color (0, 0, 0);			// nero
	
	private static int width = FrameMinesweeper.getScreenWidth()/WorldMinesweeper.getROWS(); 
	private static int height = FrameMinesweeper.getScreenHeight()/WorldMinesweeper.getCOLS(); 
	
	public TileMinesweeper(int x, int y, BufferedImage normal, BufferedImage bomb, BufferedImage bomb_face, BufferedImage openedImage, BufferedImage flag) {
		this.x = x;
		this.y = y;
		this.normal = normal;
		this.bombImage = bomb;
		this.openedImage = openedImage;
		this.flagImage = flag;
		this.bombImage_face = bomb_face;
	}
	
	public void setOpenedImage(BufferedImage openedImage) {
		this.openedImage = openedImage;
	}
	
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	
	public boolean isOpened() {
		return opened;
	}
	
	public void setBomb(boolean bomb) {
		this.bomb = bomb;
	}
	
	public void setBombFace(boolean bomb) {
		this.bomb_face = bomb;
	}
	
	public boolean isBomb() {
		return bomb;
	}
	
	public boolean isBombFace() {
		return bomb_face;
	}
	
	public void setAmountOfNearBombs(int amountOfNearBombs) {
		this.amountOfNearBombs = amountOfNearBombs;
	}
	
	public int getAmountOfNearBombs() {
		return amountOfNearBombs;
	}
	
	public boolean canOpen() {
		return !opened && !bomb && amountOfNearBombs >= 0;
	}
	
	public void placeFlag() {
		if(flag) flag = false;
		else{
			if(!opened && WorldMinesweeper.getN_FLAGS() > 0) flag = true; // metto la bandiera solo se la casella non è già aperta e non ho raggiunto il numero massimo di bandiere inseribili
		}
	}
	
	public boolean isFlag() {
		return flag;
	}
	
	public void reset(){
		flag = false;
		bomb = false;
		opened = false;
		bomb_face = false;
	}

	public void draw(Graphics g) {
		if(!opened) {
			if(!flag) g.drawImage(normal, x * width, y * height, null);
			else g.drawImage(flagImage, x * width, y * height, null);
		} else {
			if(bomb) g.drawImage(bombImage, x * width, y * height, null);
			else if(bomb_face) g.drawImage(bombImage_face, x * width, y * height, null);
			else {
				g.drawImage(openedImage, x * width, y * height, null);
				if(amountOfNearBombs > 0) {
					
					if (amountOfNearBombs == 1) g.setColor(color1);
					else if (amountOfNearBombs == 2) g.setColor(color2);
					else if (amountOfNearBombs == 3) g.setColor(color3); 
					else if (amountOfNearBombs == 4) g.setColor(color4);
					else if (amountOfNearBombs == 5) g.setColor(color5);
					else if (amountOfNearBombs == 6) g.setColor(color6);
					else if (amountOfNearBombs == 7) g.setColor(color7);
					else g.setColor(color8);
					
					g.drawString("" + amountOfNearBombs, x * width + 7, y * height + height - 4);
				}
			}
		}
	}
	
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
}