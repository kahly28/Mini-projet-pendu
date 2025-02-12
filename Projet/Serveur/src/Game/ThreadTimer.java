package Game;

import java.net.Socket;
import java.util.Timer;

public class ThreadTimer implements Runnable {
    Socket socket;
    Joueur joueur;
    int Timer;
    boolean taskIsFinished;
    MyTimerTask myTask;
    public ThreadTimer(int time, Joueur joueur)
    {
        this.joueur = joueur;
        this.Timer = time;
        this.taskIsFinished = false;
    }



    @Override
    public void run()
    {
        Timer timer = new Timer();
        MyTimerTask task = new MyTimerTask(Timer,this);
        this.myTask = task;
        System.out.println("Vous avez " + this.Timer + " secondes");
        timer.schedule(task,0,1000);

    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void afficherTimer(int sec)
    {
        System.out.println("Il reste " + sec + " secondes");
    }

    public void stopThreadTimer() {
        this.myTask.cancel();
        Thread.currentThread().interrupt();
    }

    public void stopLaPartieEstTerminer() {
        stopThreadTimer();
    }
}


