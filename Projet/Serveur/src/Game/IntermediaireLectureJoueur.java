package Game;

import Chat.Chat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class IntermediaireLectureJoueur extends Thread {
    DataInputStream inputStream;
    Joueur joueur;
    GestionnaireCommunication gestionnaireCommunication;

    public IntermediaireLectureJoueur(Joueur joueur, GestionnaireCommunication com)
    {
        this.joueur = joueur;
        this.gestionnaireCommunication = com;
    }

    @Override
    public void run() {

        try {
            inputStream = new DataInputStream(this.joueur.getSocket().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!isInterrupted()) {
            try {
                // Boucle de réception des messages tant que le thread n'est pas interrompu
                String chaineEntrante = gestionnaireCommunication.getInputStream().readUTF();
                System.out.println(chaineEntrante);
                // Gestion du message de chat
                    if (chaineEntrante.startsWith("CHAT:")) {
                        String message = chaineEntrante.substring(5); // Extraire le message de chat
                        // Si gestionnaireCommunication a une référence à Chat, on appelle la méthode afficherMessageChat
                         Chat chat = gestionnaireCommunication.getChat();
                        if (chat != null) {
                            chat.afficherMessageChat(message);  // Transmettre le message au chat
                        }

                    }
                    // Autres messages de jeu
                    else if (chaineEntrante.equals("perdu")) {
                        String motPerdu = gestionnaireCommunication.getInputStream().readUTF(); // Le mot perdu
                        System.out.println("Vous avez perdu");
                        System.out.println("Le mot à trouver était : " + motPerdu);
                        joueur.arreter();
                        break;
                    } else if (chaineEntrante.equals("perduPartie")) {
                        String motPerdu = gestionnaireCommunication.getInputStream().readUTF(); // Le mot perdu
                        System.out.println("Vous avez perdu, un autre joueur a gagné");
                        System.out.println("Le mot à trouver était : " + motPerdu);
                        joueur.arreter();
                        break;
                    } else if (chaineEntrante.equals("gagne")) {
                        System.out.println("Vous avez gagné !");
                        joueur.arreter();
                        break;
                    } else if (chaineEntrante.equals("usedLetter")) {
                        System.out.println("Cette lettre a déjà été utilisée");
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void arreterIntermediaireLectureJoueur() {
        //System.out.println("Fin du thread Game.IntermediaireLectureJoueur.");
        try {
            if (inputStream != null) inputStream.close();
            //if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }
}
