/*
 * Manages a list of drawble elements.
 */
package lu.fisch.can.widgets;

import java.util.ArrayList;

import lu.fisch.awt.Graphics;

/**
 *
 * @author robertfisch
 */
public class Drawables {
    
    private final ArrayList<Drawable> drawables = new ArrayList<>();

    public boolean add(Drawable e) {
        return drawables.add(e);
    }

    public int size() {
        return drawables.size();
    }

    public Drawable get(int index) {
        return drawables.get(index);
    }
    
    public void draw(Graphics g)
    {
        for(int i=0; i<drawables.size(); i++)
            drawables.get(i).draw(g);
    }
    
}
