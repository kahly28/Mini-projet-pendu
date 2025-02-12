package Game;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public Client()
    {

    }

    public static void main(String args[]) throws IOException {
        Socket socket = new Socket("localhost", 9111);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Scanner myObj = new Scanner(System.in);

        System.out.print("Entrez votre identifiant : ");
        String id = myObj.nextLine();

        boolean retourMenuPrincipal = true;

        while(retourMenuPrincipal == true)
        {
            String valide;
            retourMenuPrincipal = false;
            System.out.println("menu principal");
            //Le serveur demande quelle mode de jeu et le client répond
            String message = in.readUTF();
            System.out.println(message);
            myObj = new Scanner(System.in);
            System.out.println("Entrez le mode de jeu que vous souhaitez jouer : ");

            String messageOut = myObj.nextLine();

            while(!messageOut.equals("1") && !messageOut.equals("2"))
            {
                System.out.println("Mode de jeu non valide, entrez 1 pour créer, 2 pour rejoindre");
                messageOut = myObj.nextLine();
                System.out.println(messageOut);
            }
            out.writeUTF(messageOut);

            //On lit quelle mode de jeu est confirmé par le serveur
            String messageEntrant = in.readUTF();
            System.out.println("Mode recu : " + messageEntrant);

            if (Integer.parseInt(messageEntrant) == 1) {
                //Message reçu par le serveur pour demander combien de tentatives le client souhaite avoir
                message = in.readUTF();
                System.out.println(message);

                //Le client répond par un chiffre
                System.out.println("Entrez le nombre de tentatives que vous souhaitez : ");

                String nbTentative = myObj.nextLine();
                System.out.println(nbTentative);
                if (isNumeric(nbTentative)) {
                    out.writeUTF(nbTentative);

                } else {
                    while (isNumeric(nbTentative) == false) {
                        System.out.println("Entrez un chiffre");
                        System.out.println("Entrez le nombre de tentatives que vous souhaitez : ");

                        nbTentative = myObj.nextLine();

                    }
                    out.writeUTF(nbTentative);

                }

                //Timer
                messageEntrant = in.readUTF();

                //Affichage message question cb de temps max
                System.out.println(messageEntrant);

                String Timer = myObj.nextLine();
                System.out.println(Timer);

                if (isNumeric(Timer)) {
                    out.writeUTF(Timer);

                } else {
                    while (isNumeric(Timer) == false) {
                        System.out.println("Entrez un chiffre pour le timer (secondes)");
                        Timer = myObj.nextLine();

                    }
                    out.writeUTF(Timer);

                }



                //A partir de la on lance le jeu
                //Ici on attend la confirmation du client
                message = in.readUTF();
                System.out.println("message = " + message);
                if (message.equals("confirme")) {
                    System.out.println(message);
                    System.out.println("Demaragge joueur");
                    Joueur joueur = new Joueur(socket,true,id);
                    joueur.start();
                }

            } else if (Integer.parseInt(messageEntrant) == 2) {
                String salon = in.readUTF();
                System.out.println(salon);

                String CreationOuRejoindre = myObj.nextLine();
                System.out.println(CreationOuRejoindre);

                while (!CreationOuRejoindre.equals("1") && !CreationOuRejoindre.equals("2"))
                {
                    System.out.println("Créer salon ou rejoindre ? Entrez 1 ou 2");
                    CreationOuRejoindre = myObj.nextLine();
                    System.out.println(CreationOuRejoindre);
                }

                //Mode de jeu entré
                out.writeUTF(CreationOuRejoindre);

                int nbSalon = Integer.parseInt(CreationOuRejoindre);

                if (nbSalon == 1) {

                    boolean idValide = false;

                    while (!idValide) {
                        System.out.println("Client : En attente de la demande du serveur...");
                        String messageServeur = in.readUTF(); // Lecture de la demande du serveur
                        System.out.println("Client : Message reçu : " + messageServeur);

                        System.out.println("Client : Entrez un ID : ");
                        String ID = myObj.nextLine(); // Lecture de l'ID utilisateur
                        out.writeUTF(ID); // Envoi de l'ID au serveur

                        String reponse = in.readUTF(); // Lecture de la réponse du serveur

                        // Vérifie la réponse du serveur
                        if (reponse.equals("idvalide")) {
                            System.out.println("Client : L'ID est valide !");
                            idValide = true; // Sort de la boucle
                        } else if (reponse.equals("idnonvalide")) {
                            System.out.println("Client : L'ID est déjà utilisé, veuillez en entrer un autre.");
                        }
                    }

                    //Choix nb tentatives
                    messageEntrant = in.readUTF();
                    System.out.println(messageEntrant);

                    String nbTentative = myObj.nextLine();
                    System.out.println(nbTentative);
                    if (isNumeric(nbTentative)) {
                        out.writeUTF(nbTentative);
                    } else {
                        while (isNumeric(nbTentative) == false) {
                            System.out.println("Entrez un chiffre");
                            System.out.println("Entrez le nombre de tentatives que vous souhaitez : ");
                            nbTentative = myObj.nextLine();
                        }
                        out.writeUTF(nbTentative);
                    }

                    //Timer
                    messageEntrant = in.readUTF();
                    //Affichage message question cb de temps max
                    System.out.println(messageEntrant);
                    String Timer = myObj.nextLine();
                    System.out.println(Timer);
                    if(isNumeric(Timer))
                    {
                        out.writeUTF(Timer);
                    }
                    else {
                        while(isNumeric(Timer) == false)
                        {
                            System.out.println("Entrez un chiffre pour le timer (secondes)");
                            Timer = myObj.nextLine();
                        }
                        out.writeUTF(Timer);
                    }



                    //Le joueur va rejoindre le salon créer
                    System.out.println("Demaragge joueur");
                    Joueur joueur = new Joueur(socket,true,id);
                    joueur.start();


                    /*
                    // Game.Partie du code serveur où l'hôte entre "start"
                    System.out.println("Vous êtes l'hôte de la partie.");
                    System.out.println("Entrez 'start' pour démarrer la partie.");

                    String startMessage = "";
                    while (!startMessage.equals("start")) {
                        startMessage = myObj.nextLine();  // Attente de la saisie de l'hôte
                        if (!startMessage.equals("start")) {
                            System.out.println("Veuillez entrer 'start' pour démarrer la partie.");
                        }
                    }

                    // Une fois que 'start' est reçu, on peut commencer la partie
                    out.writeUTF("start");  // Envoi du message 'start' au serveur
                    System.out.println("La partie commence maintenant.");

                     */


                } else if (nbSalon == 2) {
                    //Id partie
                    messageEntrant = in.readUTF();
                    System.out.println(messageEntrant);

                    String idPartie = myObj.nextLine();
                    String messageSortant = idPartie;
                    out.writeUTF(messageSortant);

                    messageEntrant = in.readUTF();

                    while (messageEntrant.equals("partieNonTrouve") && !messageEntrant.equals("q")) {
                        System.out.println("Game.Partie non trouvé veuillez entrer un autre id");
                        System.out.println("Pour revenir au menu initial écrivez q");
                        id = myObj.nextLine();
                        messageSortant = id;
                        out.writeUTF(messageSortant);

                        messageEntrant = in.readUTF();
                        System.out.println("message entrant : " + messageEntrant);
                    }

                    if (messageEntrant.trim().equals("partieTrouve")) {
                        System.out.println("La partie commence");
                        Joueur joueur = new Joueur(socket,false,id);
                        joueur.start();

                    }
                    else
                    {
                        System.out.println("retour menu principal");
                        retourMenuPrincipal = true;
                    }
                }

            }

        }



    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


}
