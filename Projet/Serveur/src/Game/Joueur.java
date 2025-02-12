package Game;

import Chat.Chat;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

import static Game.Client.isNumeric;
import static javafx.application.Application.launch;

public class Joueur extends Thread{
    private volatile boolean running = true; // Drapeau pour arrêter proprement
    String id;
    boolean isHote;
    IntermediaireLectureJoueur intermediaire;
    boolean partieCommence = false;

    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    private BufferedReader inputReader;
    ThreadTimer myThreadTimer;
    GestionnaireCommunication gestionnaireCommunication;


    public Joueur(Socket socket, boolean hote,String id) throws IOException {
        this.socket = socket;
        this.isHote = hote;
        this.id = id;
        this.gestionnaireCommunication = new GestionnaireCommunication(this); // Gestionnaire central

    }

    @Override
    public void run() {
        String lettreEnvoye = null;
        try {
            inputReader = new BufferedReader(new InputStreamReader(System.in));
            inputStream = new DataInputStream(this.socket.getInputStream());
            outputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Si le joueur est l'hôte, demander le démarrage de la partie
        if (isHote) {
            System.out.println("Vous êtes l'hôte. Tapez 'start' pour commencer la partie : ");
            while (!partieCommence) {
                try {
                    String commande = inputReader.readLine();
                    if ("start".equalsIgnoreCase(commande)) {
                        outputStream.writeUTF("start"); // Envoyer le message "start" au serveur
                        partieCommence = true;
                    } else {
                        System.out.println("Tapez 'start' pour démarrer la partie.");
                    }
                } catch (IOException e) {
                    System.out.println("Erreur lors de l'envoi de 'start' : " + e.getMessage());
                    break;
                }
            }
        } else {
            // Si le joueur n'est pas l'hôte, attendre le message "start" du serveur
            System.out.println("En attente que l'hôte démarre la partie...");
            while (!partieCommence) {
                try {
                    String message = inputStream.readUTF();
                    if ("start".equalsIgnoreCase(message)) {
                        partieCommence = true;
                        System.out.println("La partie a commencé !");
                    }
                } catch (IOException e) {
                    System.out.println("Erreur en attendant le début de la partie : " + e.getMessage());
                    break;
                }
            }
        }

        while (true) {
            try {
                if (!(System.in.available() > 0)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                System.in.read(); // Lire et ignorer les caractères
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int timer;
        try {
            timer = Integer.parseInt(inputStream.readUTF());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ThreadTimer myTimer = new ThreadTimer(timer,this);
        this.myThreadTimer = myTimer;
        myTimer.run();

        Chat myChat = new Chat(this);
        this.gestionnaireCommunication.setChat(myChat);
        intermediaire = new IntermediaireLectureJoueur(this,this.gestionnaireCommunication);
        intermediaire.start();

        System.out.println("Veuillez entrer une seule lettre ou un mot complet sans espaces, caractères spéciaux, chiffres ou accents. Toute autre entrée sera considérée comme invalide.");
        while(running)
        {
            try
            {
                lettreEnvoye = inputReader.readLine();
                if(!lettreEnvoye.matches("[a-zA-Z]+"))
                {
                    while(!lettreEnvoye.matches("[a-zA-Z]+"))
                    {
                        System.out.println("L'entrée est invalide (doit être une lettre ou un mot sans espaces");
                        lettreEnvoye = inputReader.readLine();
                    }

                }
                // Vérifier si l'entrée est nulle ou vide avant d'envoyer
                if (lettreEnvoye != null && !lettreEnvoye.trim().isEmpty()) {
                    // Envoie de la réponse du client
                    outputStream.writeUTF(lettreEnvoye);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    // Méthode pour arrêter le thread
    public void arreter() {
        try {
            running = false; // Arrête la boucle
            myThreadTimer.stopLaPartieEstTerminer();
            if (intermediaire != null) intermediaire.arreterIntermediaireLectureJoueur(); // Interrupt intermediaire first
            if (inputStream != null) inputStream.close();
            System.in.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            if (inputReader != null) inputReader.close();
            if (gestionnaireCommunication != null) {
                gestionnaireCommunication.stopCommunication();
            }
        } catch (IOException e) {
            System.out.println("erreur ?");
            e.printStackTrace();
        }
       // System.out.println("Fin du thread Game.Joueur.");
        this.interrupt();
    }

    public String getid() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }



}
