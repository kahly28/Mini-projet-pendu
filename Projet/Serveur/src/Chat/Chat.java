package Chat;

import Game.Joueur;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Chat {

    private Joueur joueur; // Le joueur associé au chat
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    // Constructeur pour passer le joueur
    public Chat(Joueur joueur) {
        this.joueur = joueur;
        initialize();
    }

    private void initialize() {
        // Créer le cadre de l'application
        frame = new JFrame("Chat de " + joueur.getid());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Zone d'affichage des messages
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Champ de texte pour envoyer un message
        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        // Bouton pour envoyer le message
        sendButton = new JButton("Envoyer");
        sendButton.addActionListener(e -> envoyerMessage());
        frame.add(sendButton, BorderLayout.EAST);

        try {
            // Initialiser les streams
            this.outputStream = new DataOutputStream(joueur.getSocket().getOutputStream());
            this.inputStream = new DataInputStream(joueur.getSocket().getInputStream());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        frame.setVisible(true);
    }

    public void chargementDonnes() throws IOException {


        // Démarrer le thread de réception des messages
        new Thread(new MessageReceiver()).start();
    }

    // Classe interne pour recevoir les messages du serveur
    // Classe interne pour recevoir les messages du serveur
    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    String message = inputStream.readUTF();
                    System.out.println("Message reçu dans le chat : " + message);  // Pour le débogage
                    if (message.startsWith("CHAT:")) {
                        String chatMessage = message.substring(5);
                        // Mettre à jour l'interface graphique (JTextArea) avec le message de chat
                        SwingUtilities.invokeLater(() -> chatArea.append(chatMessage + "\n"));
                    } else {
                        System.out.println("Message non lié au chat : " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Déconnexion du serveur.");
            }
        }
    }

    // Méthode pour envoyer un message depuis le chat
    private void envoyerMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                String chatMessage = "CHAT:" + joueur.getid() + ": " + message;
                outputStream.writeUTF(chatMessage);
                outputStream.flush();
                messageField.setText("");
            } catch (IOException e) {
                System.out.println("Erreur lors de l'envoi du message.");
                e.printStackTrace();
            }
        }
    }

    public void afficherMessageChat(String message) {
        String message2 =message.substring(8);
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message2 + "\n");
        });
    }

    // Main pour démarrer le chat

}
