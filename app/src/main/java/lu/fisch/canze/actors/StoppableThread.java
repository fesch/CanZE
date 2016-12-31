package lu.fisch.canze.actors;

/**
 * Created by robertfisch on 30.12.2016.
 */

public class StoppableThread extends Thread {

    private boolean stopped = false;

    public StoppableThread(Runnable runnable) {
        super(runnable);
    }

    public void start()
    {
        stopped=false;
        super.start();
    }

    public void tryToStop()
    {
        stopped=true;
    }

    public boolean isStopped()
    {
        return stopped;
    }
}
