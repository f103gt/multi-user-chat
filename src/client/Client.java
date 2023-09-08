package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private Socket connection;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    public Client(Socket connection) {
        try {
            this.connection = connection;
            this.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            this.writer = new PrintWriter(connection.getOutputStream(), true);
        } catch (IOException e) {
            closeEverything();
        }
    }

    private void sendMessage() {
        //first of all asking for a username and then writing all the messages
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        this.username = scanner.nextLine();
        writer.println(this.username);
        while (connection.isConnected()) {
            String message = scanner.nextLine();
            writer.println(this.username + ": " + message);
        }
        closeEverything();
    }

    private void listenForMessages() {
        //listening for messages is a blocking operation, hence we want it to happen on the other thread
        new Thread(() -> {
            String messageFromChat;
            try {
                while(connection.isConnected()){
                    messageFromChat = reader.readLine();
                    System.out.println(messageFromChat);
                }
            } catch (IOException e) {
                closeEverything();
            }
        }
        ).start();
    }

    private void closeEverything() {
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

    public static void main(String[] args) throws IOException{
        Scanner scanner = new Scanner(System.in);
        Client client = new Client(new Socket("localhost",9999));
        client.listenForMessages();
        client.sendMessage();
    }

}
