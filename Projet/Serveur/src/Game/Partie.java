package Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public abstract class Partie extends Thread{
    private String id;
    private int nbTentative;
    private MonApp _monapp;
    int TypePartie;
    boolean hotePret;
    int TimeGame;



    public Partie(String id, int nbTentative,MonApp monapp,int time,int type)
    {
        this.id = id;
        this.nbTentative = nbTentative;
        this._monapp = monapp;
        this.TypePartie = type;
        this.TimeGame = time;
    }

    public int getTimeGame() {
        return TimeGame;
    }

    public boolean isHotePret() {
        return hotePret;
    }

    public int getTypePartie() {
        return TypePartie;
    }

    public String getid() {
        return id;
    }

    public int getNbTentative() {
        return nbTentative;
    }
    public void setNbTentative(int nb)
    {
        this.nbTentative = nb;
    }

    public String chooseWord() throws IOException {
        ArrayList<String> listeMot = new ArrayList<>();
        File file = new File("listedemot.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        while ((st = br.readLine()) != null) {
            listeMot.add(st.trim()); // Utilisation de trim pour enlever les espaces inutiles dès la lecture
        }
        br.close();

        Random rand = new Random();
        int rand_int1 = rand.nextInt(listeMot.size()); // Pas besoin de `size()-1`, `nextInt` gère les bornes
        String choosenWord = listeMot.get(rand_int1);

        return choosenWord.replaceAll("\\s", ""); // Supprime les espaces restants, juste au cas où
    }

    public void supprimerPartie()
    {
        _monapp.supprimerPartie(this);
    }


}
