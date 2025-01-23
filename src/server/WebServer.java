package src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Starting server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
