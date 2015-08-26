package lu.fisch.can.aligner;


import android.graphics.Point;

/**
 * Write a description of class "Area" here.
 * 
 * @author     robertfisch
 * @version    08/05/2012 15:05:07
 */
public class Area
{
	private int x = 0;

	private int y = 0;

	private int width = 0;

	private int height = 0;

	private boolean free = false;

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public boolean isFree()
	{
		return free;
	}

	public void setFree(boolean free)
	{
		this.free = free;
	}

	/**
	 * only extends to the right or to the left
	 */
	public void extend(Area area)
	{
		if (this.getX()+this.getWidth()==area.getX() &&
		    this.getHeight()==area.getHeight())
		{
			this.setWidth(this.getWidth()+area.getWidth());
		}
		else if (this.getY()+this.getHeight()==area.getY() &&
		         this.getWidth()==area.getWidth())
		{
			this.setHeight(this.getHeight()+area.getHeight());
		}
	}

	public boolean canHold(Space space)
	{
		return (space.getWidth()<=this.getWidth() &&
		        space.getHeight()<=this.getHeight());
	}

	public Area clone()
	{
		Area area = new Area();
		area.setX(x);
		area.setY(y);
		area.setWidth(width);
		area.setHeight(height);
		area.setFree(free);
		return area;
	}

    public Point getPosition()
    {
        return new Point(x,y);
    }
}