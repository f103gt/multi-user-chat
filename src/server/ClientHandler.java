package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private ArrayList<ClientHandler> clientHandlers;
    private Socket connection;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    public ClientHandler(Socket connection) {
        try {
            this.clientHandlers = new ArrayList<ClientHandler>();
            this.connection = connection;
            this.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.writer = new PrintWriter(connection.getOutputStream(), true);
            this.username = reader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + this.username + " has joined the chat!");

        } catch (IOException e) {
            closeEverything();
        }
    }

    private void broadcastMessage(String message) {
        clientHandlers.stream().
                filter(clientHandler -> !clientHandler.username.equals(this.username)).
                forEach(clientHandler -> clientHandler.writer.println(
                        clientHandler.username + ": " + message));
    }


    @Override
    public void run() {
        String userMessage;
        try {
            while (connection.isConnected()) {
                userMessage = reader.readLine();
                broadcastMessage(userMessage);
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void removeConnection() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + this.username + " has left the chat!");
    }

    private void closeEverything() {
        removeConnection();
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }

    }
}
