package lu.fisch.canze.classes;

/**
 * Created by robertfisch on 26.10.2015.
 */
public class ColorRange {
    public int from;
    public int to;
    public String color;
    public String sid;

    public ColorRange() {}

    public ColorRange(int from, int to, String color) {
        this.color = color;
        this.from = from;
        this.to = to;
    }

    public boolean isInside(double value)
    {
        return (from<=value && value<=to);
    }
}
