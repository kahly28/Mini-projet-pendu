package Game;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    public Serveur()
    {}

    public static void main(String[] args) throws Exception {
        System.out.println("Lancement du programme par Mimi et ahmet");
        ServerSocket server = new ServerSocket(9111);
        System.out.println("Le serveur a été lancé");
        int port = server.getLocalPort();
        InetAddress cetteMachine = InetAddress.getLocalHost();
        System.out.println("adresse IP du serveur de majuscule : " + cetteMachine.getHostAddress());
        System.out.println("port du serveur : " + port);


        int noConnexion = 0;
        while(true)
        {
            Socket nouveauClientSocket = server.accept();
            MonApp nouveauClientThread = new MonApp(nouveauClientSocket);
            nouveauClientThread.start();
            ++noConnexion;
            if(noConnexion > 0)
            {
                System.out.println("Connexion serveur client établie ! \n");
            }
        }
    }


}

