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
