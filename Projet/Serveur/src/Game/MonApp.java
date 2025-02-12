package Game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MonApp extends Thread{
    private static ArrayList<PartieMulti> ListePartie = new ArrayList<PartieMulti>();
    Socket socket;
    int modeJeu;
    public MonApp(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            boolean retourMenuPrincipal = true; // Drapeau pour quitter le menu principal

            while(retourMenuPrincipal == true)
            {
            //Variable qui servira de confirmation
            String valide;
            retourMenuPrincipal = false;

            //Le serveur demande au client quel mode de jeu
            String message = "Quel mode de jeu ? 1 ou 2";
            out.writeUTF(message);
            String messageEntrant = in.readUTF();


            int mode;

            System.out.println("Mode de jeu souhaité par le client : " + messageEntrant);
            mode = Integer.parseInt(messageEntrant);

            if(mode == 1 || messageEntrant.equals("q"))
            {
                int i = ListePartie.size();
                //Mode
                String envoiMode = "1";
                out.writeUTF(envoiMode);

                //Le serveur demande au client combien de tentatives il souhaite avoir
                message = "Combien de tentatives voulez-vous ? Un chiffre est attendu ";
                out.writeUTF(message);

                //Le serveur recoit la réponse du client
                String messageTentative = in.readUTF();
                System.out.println("Nb tentative entrer par le client : " + messageTentative);
                int nbTentatives = Integer.parseInt(messageTentative);

                //Timer
                message = "Combien de temps max pour la partie ? ";
                out.writeUTF(message);
                String messageTimer = in.readUTF();
                System.out.println("Temps entrer par le client : " + messageTimer);
                int timer = Integer.parseInt(messageTimer);

                //Ici nous pouvons lancer la partie
                //Envoi confirmation au client
                message = "confirme";
                out.writeUTF(message);

                //A MODIFIER
                PartieSolo partie = new PartieSolo("yo",nbTentatives,socket,this,timer,1);
                partie.start();
                //ListePartie.add(partie);
            }
            //Mode de jeu multi joueur
            else if(mode == 2)
            {
                String envoieMode = "2";
                out.writeUTF(envoieMode);

                System.out.println("Demande client création ou rejoindre salon");
                String messageSalon = "Créer salon ou rejoindre ? répondre 1 ou 2";
                out.writeUTF(messageSalon);


                String salon = in.readUTF();
                int nbSalon = Integer.parseInt(salon);
                //Créer une partie multi
                if(nbSalon == 1)
                {
                    System.out.println("Créer une partie multi");

                    //Le joueur choisi l'id de la partie
                    String messageSortant = "Choissisez l'id de la partie";
                    out.writeUTF(messageSortant);

                    String ID = null;

                    boolean idCreer = false;
                    while (!idCreer) {
                        System.out.println("Serveur : En attente de l'ID proposé par le client...");
                        messageEntrant = in.readUTF(); // Lecture de l'ID du client
                        System.out.println("Serveur : ID reçu : " + messageEntrant);

                        // Vérifie si l'ID existe déjà
                        boolean idExistant = false;
                        for (PartieMulti partie : getListePartie()) {
                            if (partie.getid().equals(messageEntrant)) {
                                idExistant = true;
                                break;
                            }
                        }

                        // Envoi de la réponse au client
                        if (idExistant) {
                            System.out.println("Serveur : L'ID existe déjà. Demande un nouvel ID.");
                            out.writeUTF("idnonvalide"); // Réponse au client
                            messageSortant = "Choissisez l'id de la partie";
                            out.writeUTF(messageSortant);

                        } else {
                            System.out.println("Serveur : L'ID est valide.");
                            out.writeUTF("idvalide"); // Réponse au client
                            ID = messageEntrant;
                            idCreer = true; // Sort de la boucle
                        }
                    }
                    //Choix nombre de tentative
                    messageSortant = "Combien de tentative pour ce salon ? Entrez un chiffre";
                    System.out.println(messageSortant);
                    out.writeUTF(messageSortant);

                    messageEntrant = in.readUTF();
                    System.out.println(messageEntrant);

                    //Timer
                    message = "Combien de temps max pour la partie ? (secondes) ";
                    System.out.println(message);
                    out.writeUTF(message);
                    String messageTimer = in.readUTF();
                    System.out.println("Temps entrer par le client : " + messageTimer);
                    int timer = Integer.parseInt(messageTimer);

                    int nbTentative = Integer.parseInt(messageEntrant);
                    PartieMulti partie = new PartieMulti(ID,nbTentative,this.socket,this,timer,1);
                    partie.start();
                    getListePartie().add(partie);

                }
                //Rejoindre une partie multi
                else if(nbSalon == 2) {
                    System.out.println("Rejoindre une partie multi");

                    String messageSortant = "Entrez l'ID de la partie que vous voulez rejoindre";
                    out.writeUTF(messageSortant);

                    messageEntrant = in.readUTF();
                    String idEntrer = messageEntrant;

                    boolean partieRejoins = false;
                    while (partieRejoins == false) {
                        for (int i = 0; i < ListePartie.size(); i++) {
                            String idPartie;
                            idPartie = ListePartie.get(i).getid();
                            System.out.println("ID de la partie : " + idPartie);
                            boolean bool = idEntrer.equals(idPartie);
                            if (bool == true) {
                                if (ListePartie.get(i).estJoignable()) {
                                    messageSortant = "partieTrouve";
                                    out.writeUTF(messageSortant);
                                    System.out.println("On rejoint la partie");
                                    ListePartie.get(i).ajouterJoueur(this.socket);
                                    partieRejoins = true;
                                    break;
                                }
                            }
                        }
                        //Game.Partie non trouvé
                        if (partieRejoins == false) {
                            System.out.println("Id partie invalide");
                            messageSortant = "partieNonTrouve";
                            out.writeUTF(messageSortant);

                            messageEntrant = in.readUTF();

                            if (messageEntrant.equals("q")) {
                                System.out.println("Le joueur veut retourner au menu principal");
                                out.writeUTF(messageEntrant);
                                retourMenuPrincipal = true;
                                break;
                            } else {
                                System.out.println(messageEntrant);
                                idEntrer = messageEntrant;

                            }

                        }
                    }

                }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static ArrayList<PartieMulti> getListePartie() {
        return ListePartie;
    }


    public void supprimerPartie(Partie partie)
    {
        System.out.println("Suppression de la partie");
        ListePartie.remove(partie);
        this.interrupt();
    }


}
