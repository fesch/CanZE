/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.awt;


public class Color 
{
	private int red   = 0;
	private int green = 0;
	private int blue  = 0;
	
	public static final Color WHITE 	= new Color(255,255,255);
	public static final Color BLACK 	= new Color(0,0,0);
	public static final Color RED   	= new Color(255,0,0);
	public static final Color GREEN   	= new Color(0,255,0);
	public static final Color GREEN_DARK= new Color(0,128,0);
	public static final Color BLUE   	= new Color(0,0,255);
	public static final Color GRAY   	= new Color(138,138,138);
	public static final Color GRAY_LIGHT= new Color(204,204,204);
	public static final Color GRAY_DARK = new Color(80,80,80);
	public static final Color YELLOW	= new Color(255,255,128);

	public static final Color RENAULT_RED    = new Color(250,36,4);
	public static final Color RENAULT_BLUE   = new Color(11,198,209);
	public static final Color RENAULT_GREEN  = new Color(133,196,26);
	public static final Color RENAULT_YELLOW = new Color(251,207,00);

	public Color()
	{
	}
	
	public Color(int red, int green, int blue)
	{
		this.red=red;
		this.green=green;
		this.blue=blue;
	}
	
	public int getAndroidColor()
	{
		return android.graphics.Color.rgb(red, green, blue);
	}
	
	// original JDK code
	public static Color decode(String nm) throws NumberFormatException 
	{
			Integer intval = Integer.decode(nm);
			int i = intval.intValue();
			return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
	}
	
	public int getRed()
	{
		return red;
	}
	
	public int getGreen()
	{
		return green;
	}
	
	public int getBlue()
	{
		return blue;
	}
	
}
