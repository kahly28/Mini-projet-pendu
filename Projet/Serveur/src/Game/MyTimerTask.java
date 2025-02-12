package Game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    int Time;
    int compteur = 0;
    ThreadTimer thread;
    Socket socket;
    public MyTimerTask(int time, ThreadTimer thread)
    {
        this.Time = time;
        this.thread = thread;
    }

    @Override
    public void run() {
        Time = Time-1;
        compteur++;

        if(compteur == 5)
        {
            thread.afficherTimer(Time);
            compteur = 0;
        }

        if(Time == 0 )
        {
            System.out.println("my timer task = 0");
            try {
                DataOutputStream outputStream = new DataOutputStream(this.thread.getJoueur().getSocket().getOutputStream());
                outputStream.writeUTF("chronoA0");
                thread.stopThreadTimer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
