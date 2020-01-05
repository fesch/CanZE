package lu.fisch.canze.classes;

import lu.fisch.canze.activities.MainActivity;

public class Activity {
    private Class classOf;
    private int drawable;
    private String title;
    private int id;

    /*public Activity(String title, int drawable, Class classOf) {
        this.classOf = classOf;
        this.drawable = drawable;
        this.title = title;
    }*/

    public Activity(int id, int titleId, int drawable, Class classOf) {
        this.classOf = classOf;
        this.drawable = drawable;
        this.title = MainActivity.getStringSingle(titleId).toUpperCase();
        this.id = id;
    }

    public Class getClassOf() {
        return classOf;
    }

    public int getDrawable() {
        return drawable;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString()
    {
        return title;
    }
}
