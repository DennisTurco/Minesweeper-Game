package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TileMineField
{
	private BufferedImage normal;
	private BufferedImage openedImage;
	private BufferedImage flagImage;
	private BufferedImage bombImage;
	
	private int x;
	private int y;
	private boolean bomb;
	private boolean opened;
	private boolean flag;
	private int amountOfNearBombs;
	
	private static int width = FrameMineField.getWidth()/WorldMineField.getROWS(); 
	private static int height = FrameMineField.getEight()/WorldMineField.getCOLS(); 
	
	public TileMineField(int x, int y, BufferedImage normal, BufferedImage bomb, BufferedImage openedImage, BufferedImage flag)
	{
		this.x = x;
		this.y = y;
		this.normal = normal;
		this.bombImage = bomb;
		this.openedImage = openedImage;
		this.flagImage = flag;
	}
	
	public void setOpenedImage(BufferedImage openedImage)
	{
		this.openedImage = openedImage;
	}
	
	public void setOpened(boolean opened)
	{
		this.opened = opened;
	}
	
	public boolean isOpened()
	{
		return opened;
	}
	
	public void setBomb(boolean bomb)
	{
		this.bomb = bomb;
	}
	
	public boolean isBomb()
	{
		return bomb;
	}
	
	public void setAmountOfNearBombs(int amountOfNearBombs)
	{
		this.amountOfNearBombs = amountOfNearBombs;
	}
	
	public int getAmountOfNearBombs()
	{
		return amountOfNearBombs;
	}
	
	public boolean canOpen()
	{
		return !opened&&!bomb&&amountOfNearBombs >= 0;
	}
	
	public void placeFlag()
	{
		if(flag) flag = false;
		else
		{
			if(!opened) flag = true;
		}
	}
	
	public boolean isFlag()
	{
		return flag;
	}
	
	public void reset()
	{
		flag = false;
		bomb = false;
		opened = false;
	}

	public void draw(Graphics g)
	{
		if(!opened) 
		{
			if(!flag) g.drawImage(normal, x * width, y * height, null);
			else g.drawImage(flagImage, x * width, y * height, null);
		}
		else 
		{
			if(bomb) g.drawImage(bombImage, x * width, y * height, null);
			else
			{
				g.drawImage(openedImage, x * width, y * height, null);
				if(amountOfNearBombs > 0)
				{
					g.setColor(Color.WHITE);
					g.drawString("" + amountOfNearBombs, x * width + 7, y * height + height - 4);
				}
			}
		}
	}
	
	public static int getWidth()
	{
		return width;
	}
	
	public static int getHeight()
	{
		return height;
	}
}