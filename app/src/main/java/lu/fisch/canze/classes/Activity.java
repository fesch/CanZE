package lu.fisch.canze.classes;

public class Activity {
    private Class classOf;
    private String drawable;
    private String title;

    public Activity(String title, String drawable, Class classOf) {
        this.classOf = classOf;
        this.drawable = drawable;
        this.title = title;
    }

    public Class getClassOf() {
        return classOf;
    }

    public String getDrawable() {
        return drawable;
    }

    public String getTitle() {
        return title;
    }
}
