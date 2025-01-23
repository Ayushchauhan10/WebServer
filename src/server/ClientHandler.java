package src.server;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestLine;
            while ((requestLine = in.readLine()) != null) {
                if (requestLine.isEmpty()) break;

                System.out.println("Request: " + requestLine);
                StringTokenizer tokenizer = new StringTokenizer(requestLine);
                String method = tokenizer.nextToken();
                String resource = tokenizer.nextToken();

                if (resource.startsWith("/api")) {
                    APIHandler.handleAPIRequest(method, resource, out);
                } else if (resource.startsWith("/message")) {
                    MessageHandler.handleMessageRequest(resource, out);
                } else {
                    StaticFileHandler.handleStaticFile(resource, out);
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
