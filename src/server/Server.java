package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService pool;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        this.pool = Executors.newCachedThreadPool();
    }

    public void start(){
        try {
            while(!serverSocket.isClosed()){
                Socket connection = serverSocket.accept();
                System.out.println("A new client has connected!");
                ClientHandler clientHandler = new ClientHandler(connection);
                pool.submit(clientHandler);
            }
        } catch (IOException e) {
            shutdownServer();
        }
    }

    private void shutdownServer(){
        try {
            if(pool != null){
                pool.shutdown();
            }
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }

    }

    public static void main(String[] args) throws IOException{
        Server server = new Server(new ServerSocket(9999));
        server.start();
    }
}
