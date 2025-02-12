package Game;

import Chat.Chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GestionnaireCommunication {
    private Joueur joueur;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Chat chat;  // Référence à l'objet Chat


    public GestionnaireCommunication(Joueur joueur) throws IOException {
        this.joueur = joueur;
        this.inputStream = new DataInputStream(this.joueur.getSocket().getInputStream());
        this.outputStream = new DataOutputStream(this.joueur.getSocket().getOutputStream());
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }

    public void stopCommunication() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            /*
            if (socket != null) {
                socket.close();
            }

             */
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture de la communication : " + e.getMessage());
        }
    }
}
