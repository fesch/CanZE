package lu.fisch.canze.aligner;

import java.util.ArrayList;
import java.util.Collections;

import lu.fisch.awt.Color;
import lu.fisch.awt.Graphics;

/**
 * Write a description of class "Grille" here.
 * 
 * @author     robertfisch
 * @version    08/05/2012 15:07:21
 */
public class Grille
{
    private final int padding = 10;
    
	// make somehow sorted
	private final ArrayList<Integer> xs = new ArrayList<>();
	private final ArrayList<Integer> ys = new ArrayList<>();

	private final ArrayList<Space> spaces = new ArrayList<>();

	public Grille()
	{
		xs.add(0);
		ys.add(0);
	}

	public int getRowCount()
	{
		return ys.size()-1;
	}
	
	public int getColCount()
	{
		return xs.size()-1;
	}
	
	public void addSpace(Space space)
	{
		spaces.add(space);
		
		int x1 = space.getX();
		int x2 = x1+space.getWidth();

		int y1 = space.getY();
		int y2 = y1+space.getHeight(); 

		if(!xs.contains(x1)) xs.add(x1);
		if(!xs.contains(x2)) xs.add(x2);

		if(!ys.contains(y1)) ys.add(y1);
		if(!ys.contains(y2)) ys.add(y2);
                		
		Collections.sort(xs);
		Collections.sort(ys);
	}

	public Area getArea(int x, int y)
	{
		Area area = new Area();
		area.setX(xs.get(x));
		area.setY(ys.get(y));
		area.setWidth(xs.get(x+1)-xs.get(x));
		area.setHeight(ys.get(y+1)-ys.get(y));
		boolean free = true;
		for(int i=0;i<spaces.size();i++)
		{
			Space s = spaces.get(i);
			if ( (s.getX()<=area.getX()) &&
			     (area.getX()+area.getWidth()<=s.getX()+s.getWidth()) &&
			     (s.getY()<=area.getY()) &&
			     (area.getY()+area.getHeight()<=s.getY()+s.getHeight()))
			{
				free=false;
			}
		}
		area.setFree(free);
		return area;
	}

	public Area findFreeAreaFor(Space space)
	{
		int x=0;
		int y=0;
		int dirX = +1;
		int dirY = -1;
                MySpace mySpace = new MySpace(0,0,space.getWidth()+2*padding,space.getHeight()+2*padding);
                space = mySpace;
		if(getColCount()!=0 && getRowCount()!=0)
		{
			do
			{
				Area area = getArea(x,y);
				if(area.isFree())
				{	// it fits immediately
					if(area.getHeight()>=space.getHeight() &&
					   area.getWidth()>=space.getHeight())
					{
                                                area.setX(area.getX()+padding);
                                                area.setY(area.getY()+padding);
						return area;
					}
					else
					{
                                                //System.out.println();
                                                //System.out.println("Extending "+(x)+","+(y));
						// extend by X
						boolean stop = false;
						int addX=0;
						while(area.getWidth()<space.getWidth() && !stop)
						{
							addX++;
                                                        //System.out.println("Right "+(x+addX)+","+(y));
							if(x+addX>=getColCount()) stop=true;
							else
							{
								Area other = getArea(x+addX,y);
								stop=!other.isFree();
								area.extend(other);
							}
						}
						// only continue if we got something width enought!
						if(!stop)
						{
							stop = false;
							int addY = 0;
							// extend by Y
							while(area.getHeight()<space.getHeight() && !stop)
							{
								addY++;
                                                                //System.out.println("Down "+(x)+","+(y+addY));
								if(y+addY>=getRowCount()) stop=true;
								else
								{
									Area other = getArea(x,y+addY);
									stop=!other.isFree();
									if(!stop)
									{
										for(int ax=1;ax<=addX;ax++)
										{   
                                                                                        //System.out.println("Down-Right "+(x+ax)+","+(y+addY));
											Area right = getArea(x+ax,y+addY);
											if(!right.isFree()) stop=true;
											other.extend(right);
										}
									}
									area.extend(other);
								}
							}
							if(!stop)
							{
                                                                area.setX(area.getX()+padding);
                                                                area.setY(area.getY()+padding);
								return area;
							}
						}
					}
					
				}
				
				x+=dirX;
				y+=dirY;
				
				if(x>=getColCount())
				{
					x=getColCount()-1;
					dirX=-dirX;
					dirY=-dirY;
					y+=2*dirY;
				}
				else if(y>=getRowCount())
				{
					y=getRowCount()-1;
					dirX=-dirX;
					dirY=-dirY;
					x+=2*dirX;	
				}
				else if(x<0)
				{
					x=0;
					dirX=-dirX;
					dirY=-dirY;
				}
				else if(y<0)
				{
					y=0;
					dirX=-dirX;
					dirY=-dirY;
				}
				
			}
			while(x<getColCount() && y<getRowCount());
	
			Area right = getArea(getColCount()-1,0);
			Area down = getArea(0,getRowCount()-1);
                        Area area = right;
                        
                        if(right.getX()+right.getWidth()>down.getY()+down.getHeight())
                        {
                            area=down;
                            area.setY(area.getY()+area.getHeight());
                        }
                        else
                            area.setX(area.getX()+area.getWidth());
                        area.setX(area.getX()+padding);
                        area.setY(area.getY()+padding);
			return area;
		}
		Area a = new Area();
		a.setX(padding);
		a.setY(padding);
		return a;
	}

	public void paint(Graphics g, int width, int height)
	{
		for(int x=0;x<getColCount();x++)
		{
			for(int y=0;y<getRowCount();y++)
			{
				Area a = getArea(x,y);
				if(a.isFree())
					g.setColor(Color.GREEN);
				else
					g.setColor(Color.RED);
				g.fillRect(a.getX(),a.getY(),a.getWidth(),a.getHeight());
			}
		}
		/*
		g.setColor(Color.YELLOW);
		for(int i=0;i<spaces.size();i++)
		{
			Space space = spaces.get(i);
			g.fillRect(space.getX(),space.getY(),space.getWidth(),space.getHeight());
		}/**/

		g.setColor(Color.BLACK);
		for(int i=0;i<xs.size();i++)
		{
			int x = xs.get(i);
			g.drawLine(x,0,x,height);
		}
		for(int i=0;i<ys.size();i++)
		{
			int y = ys.get(i);
			g.drawLine(0,y,width,y);
		}
	}
	
}