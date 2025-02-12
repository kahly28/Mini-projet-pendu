package Game;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class PartieSolo extends Partie
{
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    public PartieSolo(String id, int nbTentative, Socket socket,MonApp monapp,int time,int type)
    {
        super(id,nbTentative,monapp,time,type);
        this.socket = socket;
    }


    @Override
    public void run() {
        String wordToFind;

        try {
            inputStream = new DataInputStream(this.socket.getInputStream());
            outputStream = new DataOutputStream(this.socket.getOutputStream());
            wordToFind = chooseWord();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        hotePret = false;
        while(!hotePret)
        {
            try {
                String in = inputStream.readUTF();
                if(in.equalsIgnoreCase("start"))
                {
                    hotePret = true;
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

        }


        int wordLength = wordToFind.length();
        System.out.println("Le mot a une longueur de " + wordLength);

        //Initialiser le tableau qui sera échangé
        char[] array = new char[wordLength];
        Arrays.fill(array, '_');

        String motDevine = new String(array);

        System.out.println("Mot a devine : " + wordToFind);
        System.out.println(motDevine);
        int nbVie = getNbTentative();
        ArrayList<Character> usedLetter = new ArrayList<Character>();

        try {
            outputStream.writeUTF(String.valueOf(getTimeGame()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (!isInterrupted())
        {
            String chaineEntrante;
            try
            {
                String nbTentative = "Nombre vie : " + nbVie;
                outputStream.writeUTF("Etat actuel : " +motDevine);
                outputStream.writeUTF(nbTentative);
                chaineEntrante = String.valueOf(inputStream.readUTF());
                System.out.println(chaineEntrante);

                boolean trouve = false;

                //Si la chaine est un mot
                if(chaineEntrante.equals("chronoA0"))
                {
                    System.out.println("Vous avez perdu");
                    String perdu = "perdu";
                    outputStream.writeUTF(perdu);
                    outputStream.writeUTF(wordToFind);
                    this.interrupt();
                    break;
                }
                if(chaineEntrante.length() > 1)
                {
                    //Le joueur n'a trouvé le bon mot
                    if(!chaineEntrante.equals(wordToFind))
                    {
                        trouve = false;
                        int nb = getNbTentative();
                        setNbTentative(nb-1);
                    }
                    //Le joueur a trouvé le bon mot
                    else
                    {
                        System.out.println("Le joueur a gagné");
                        String gagne = "gagne";
                        outputStream.writeUTF(gagne);
                        outputStream.writeUTF("Vous avez trouvé le mot ! : " + wordToFind);
                        this.interrupt();
                        break;
                    }
                }

                //Si la chaine est une lettre
                else
                {
                    int lettreTrouve =0;
                    System.out.println(usedLetter);
                    //On verifie si la lettre a deja été utilisé
                    for(int i = 0;i<usedLetter.size();i++ )
                    {
                        if(usedLetter.get(i) == chaineEntrante.charAt(0))
                        {
                            System.out.println("Lettre déjà utilisée");
                            lettreTrouve =1;
                        }
                    }

                    if(lettreTrouve == 1)
                    {
                        String lettreUtilise = "usedLetter";
                        outputStream.writeUTF(lettreUtilise);
                    }
                    //Si la lettre n'a pas été utilisé
                    else if(lettreTrouve == 0)
                    {

                        for (int i = 0; i < wordToFind.length(); i++)
                        {
                            if (wordToFind.charAt(i) == chaineEntrante.charAt(0))
                            {
                                System.out.println("Lettre trouvée à : " + i);
                                char[] motDevineArray = motDevine.toCharArray();
                                motDevineArray[i] = chaineEntrante.charAt(0);
                                motDevine = String.valueOf(motDevineArray);
                                trouve = true;
                                usedLetter.add(wordToFind.charAt(i));
                                System.out.println("after add");
                                System.out.println(usedLetter);
                            }
                        }
                    }

                    
                }

                //Le joueur a trouvé la lettre manquante
                if(motDevine.equals(wordToFind))
                {
                    String gagne = "gagne";
                    outputStream.writeUTF(gagne);
                    outputStream.writeUTF("Vous avez trouvé le mot ! : " + wordToFind);
                    this.interrupt();
                    break;
                }

                boolean contains = false;
                for(int i = 0;i<usedLetter.size();i++ )
                {
                    if(usedLetter.get(i) == chaineEntrante.charAt(0))
                    {
                        contains = true;
                    }

                }
                usedLetter.add(chaineEntrante.charAt(0));
                //Si la lettre proposé par le joueur n'est ni dans dans le mot à deviner ni dans la liste des lettres déjà utilisées
                //alors on enleve une vie
                if(trouve == false && contains == false)
                {
                    nbVie--;
                }

                //Cas ou le joueur a perdu;
                if(nbVie == 0)
                {
                    System.out.println("Le joueur a perdu");
                    String perdu = "perdu";
                    outputStream.writeUTF(perdu);
                    outputStream.writeUTF(wordToFind);
                    this.interrupt();
                    break;
                }

            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }


        }
        arreterPartieSolo();
    }

    void arreterPartieSolo()
    {
        try {
            System.out.println("Fermeture de tous les flux");
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            supprimerPartie();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }
}
