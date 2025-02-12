package Game;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class IntermediairePartie extends Thread
{

    //ArrayList<Socket> ListeJoueurs = new ArrayList<Socket>();
    PartieMulti partie;
    public IntermediairePartie(PartieMulti partieMulti)
    {
        this.partie = partieMulti;
       // this.ListeJoueurs = partieMulti.getListeJoueur();
    }

    @Override
    public void run() {
        while(!isInterrupted())
        {
            for (Socket joueurSocket : this.partie.getListeJoueur()) {
                try
                {
                    DataInputStream inputStream = new DataInputStream(joueurSocket.getInputStream());
                    if (inputStream.available() > 0)
                    {
                        String message = inputStream.readUTF();
                        // Vérification si c'est un message de chat
                        if (message.startsWith("CHAT:")) {
                            System.out.println("CHAT recu");
                            System.out.println(message);
                            // Envoie le message de chat directement à la méthode du chat
                            partie.recevoirMessageChat(message.substring(5), joueurSocket);
                        } else {
                            // Autrement, il s'agit d'un message de jeu
                            partie.setEventOccured(message, joueurSocket);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        arreterIntermediairePartie();
    }

    public void arreterIntermediairePartie()
    {

    }
}
