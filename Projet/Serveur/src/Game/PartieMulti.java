package Game;

import Chat.Chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;


public class PartieMulti extends Partie{
    IntermediairePartie intermediaire;
    Socket hotePartie;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    boolean eventOccured = false;  // Variable de signalisation
    String latestGuess = "";       // Dernière proposition de lettre ou mot
    String wordToFind = "";
    String motDevine = "";
    ArrayList<Character> usedLetter = new ArrayList<Character>();
    Socket latestSocket;




    ArrayList<Socket> listeJoueur = new ArrayList<Socket>();

    public ArrayList<Socket> getListeJoueur()
    {
        return listeJoueur;
    }

    public PartieMulti(String id, int nbTentative, Socket socket,MonApp monapp,int time,int type)
    {
        super(id,nbTentative,monapp,time,type);
        hotePartie = socket;
    }

    public void ajouterJoueur(Socket client)
    {
        listeJoueur.add(client);
    }

    public boolean estJoignable()
    {
        return !hotePret;
    }


    @Override
    public void run() {
        System.out.println("Game.Partie créer");

        ajouterJoueur(this.hotePartie);

        String wordToFind;

        try {
            inputStream = new DataInputStream(this.hotePartie.getInputStream());
            outputStream = new DataOutputStream(this.hotePartie.getOutputStream());
            wordToFind = chooseWord();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Le serveur va attendre que des joueurs rejoignent la partie
        //jusqu'à ce que l'hôte démarre la partie
        hotePret = false;

        while (!hotePret) {
            for (Socket joueurSocket : listeJoueur) {
                try {
                    DataInputStream joueurInputStream = new DataInputStream(joueurSocket.getInputStream());
                    String message = joueurInputStream.readUTF();

                    // Vérifiez si c'est l'hôte et si le message est "start"
                    if (joueurSocket == hotePartie && message.equalsIgnoreCase("start")) {
                        hotePret = true;
                        System.out.println("L'hôte a démarré la partie");
                        break; // Sortir de la boucle dès que l'hôte démarre la partie
                    } else {
                        System.out.println("Message ignoré d'un joueur non-hôte");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        for (Socket joueurSocket : listeJoueur) {
            if (joueurSocket != hotePartie)
            {
                try {
                    outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                    outputStream.writeUTF("start");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int wordLength = wordToFind.length();

        //Initialiser le tableau qui sera échangé
        char[] array = new char[wordLength];
        Arrays.fill(array, '_');

        this.wordToFind = wordToFind;

        this.motDevine = new String(array);

        System.out.println("Mot a devine : " + wordToFind);
        System.out.println(motDevine);
        int nbVie = getNbTentative();
        ArrayList<Character> usedLetter = new ArrayList<Character>();

        for(Socket jouerSocket : listeJoueur)
        {
            try{
                outputStream = new DataOutputStream(jouerSocket.getOutputStream());
                outputStream.writeUTF(String.valueOf(getTimeGame()));
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        this.intermediaire = new IntermediairePartie(this);
        intermediaire.start();

        for (Socket joueurSocket : listeJoueur) {
            try {
                outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                String nbTentative = "Nombre vie : " + getNbTentative();
                outputStream.writeUTF("État actuel : " + motDevine);
                outputStream.writeUTF(nbTentative);
                System.out.println("Envoie premier message");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (!isInterrupted() && getNbTentative() > 0 && !motDevine.toString().equals(wordToFind))
        {
            synchronized (this) {
                if (!eventOccured) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Traiter a derniere proposition d'un joueur
            if(this.latestGuess.equals("chronoA0"))
            {
                envoyerMessageTous("chronoA0");
            }
            else
            {
                processGuess(this.latestGuess);
            }
            eventOccured = false;
        }
        arreterPartieMulti();
    }


    public synchronized void setEventOccured(String proposition,Socket socket) {
        this.latestGuess = proposition;
        this.eventOccured = true;
        this.latestSocket = socket;
        notify();  // Notifie le thread principal qu'un événement est survenu
        System.out.println("Le programme recoit la proposition");
    }

    public synchronized void processGuess(String proposition) {
        System.out.println("Proposition : " + proposition);
        if (proposition.length() > 1) {
            if (proposition.equals(wordToFind))
            {
                System.out.println("mot complet trouve");
                motDevine = wordToFind;
                envoyerMessageTous("gagne");
            }
            else
            {
                int nb = getNbTentative();
                setNbTentative(nb-1);
                envoyerMessageTous(motDevine);
            }
        }
        else
        {
            if (this.usedLetter.contains(this.latestGuess.charAt(0)))
            {
                envoyerMessageTous("usedLetter");
            }
            else
            {
                usedLetter.add(latestGuess.charAt(0));
                boolean trouve = false;
                for (int i = 0; i < wordToFind.length(); i++)
                {
                    if (wordToFind.charAt(i) == latestGuess.charAt(0))
                    {
                        System.out.println("Lettre trouvé a : " + i);
                        char[] motDevineArray = motDevine.toCharArray();
                        motDevineArray[i] = proposition.charAt(0);
                        motDevine = String.valueOf(motDevineArray);
                        trouve = true;
                    }
                }
                if (!trouve)
                {
                    System.out.println("Non trouvé");
                    int nb = getNbTentative();
                    setNbTentative(nb-1);
                }
                if(trouve && motDevine.equals(wordToFind))
                {
                    envoyerMessageTous("gagne");
                }
                envoyerMessageTous(motDevine);
            }
        }
        if(getNbTentative() <= 0)
        {
            System.out.println("Plus de vie");
            envoyerMessageTous("perdu");
        }


    }

    private void envoyerMessageTous(String message) {
        if(message.equals("gagne"))
        {
            System.out.println("Gestion envoie message un joueur a gagné");
            for(Socket joueurSocket : listeJoueur)
            {
                try
                {
                    DataOutputStream joueurOutputStream = new DataOutputStream(joueurSocket.getOutputStream());
                    if (joueurSocket == latestSocket)
                    {
                        System.out.println("SOCKET = LATEST SOCKET");
                        joueurOutputStream.writeUTF("gagne");
                        joueurOutputStream.writeUTF("Vous avez gagné ! Le mot était " + wordToFind);
                        System.out.println("laaa ? ");
                    }
                    else
                    {
                        envoyerMessageTous("perduPartie");
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
        else if(message.equals("perdu"))
        {
            System.out.println("Nombre de tentavie egal a 0, personne n'a gagne");
            String perdu = "perdu";
            for(Socket joueurSocket : listeJoueur) {
                try {
                    outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                    outputStream.writeUTF(perdu);
                    outputStream.writeUTF(wordToFind);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.intermediaire.interrupt();
            this.interrupt();
        }
        else if(message.equals("perduPartie"))
        {
            System.out.println("Vous avez perdu");
            String perdu = "perduPartie";
            for(Socket joueurSocket : listeJoueur) {
                if(joueurSocket != latestSocket) {
                    try {
                        outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                        outputStream.writeUTF(perdu);
                        outputStream.writeUTF(wordToFind);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            this.intermediaire.interrupt();
            this.interrupt();
        }
        else if(message.equals("usedLetter"))
        {
            System.out.println("Cas usedLetter");
            String newMessage = "Lettre déjà utilisé, veuillez en saisir une autre.";
            String newMessage2 = "Nombre vie : " + getNbTentative() + ", état actuel : " + this.motDevine;
            for(Socket joueurSocket : listeJoueur)
            {
                try
                {
                    outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                    outputStream.writeUTF(newMessage);
                    outputStream.writeUTF(newMessage2);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }
        else if(message.equals("chronoA0"))
        {
            envoyerMessageTous("perdu");
        }
        else {
            System.out.println("else");
            for (Socket joueurSocket : listeJoueur) {
                try {
                    outputStream = new DataOutputStream(joueurSocket.getOutputStream());
                    String nbTentative = "Nombre vie : " + getNbTentative();
                    outputStream.writeUTF("État actuel : " + message);
                    outputStream.writeUTF(nbTentative);
                    System.out.println("Envoie message");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void recevoirMessageChat(String message, Socket senderSocket) {
        String senderId = "Joueur"; // Vous pouvez améliorer pour récupérer un vrai ID
        String messageChat = senderId + ": " + message;

        // Transmet le message de chat à tous les autres joueurs
        for (Socket joueurSocket : listeJoueur) {
            try {
                DataOutputStream joueurOutputStream = new DataOutputStream(joueurSocket.getOutputStream());
                joueurOutputStream.writeUTF("CHAT:" + messageChat);
                System.out.println("envoie de CHAT: " + messageChat);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void arreterPartieMulti() {
        try {
            for (Socket socket : listeJoueur) {
                if (socket != null) socket.close();
            }
            if(inputStream != null) inputStream.close();
            if(outputStream != null) outputStream.close();
            if(intermediaire != null) intermediaire.interrupt();
            supprimerPartie();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
