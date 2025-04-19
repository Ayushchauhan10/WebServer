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
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestLine = in.readLine();

            if (requestLine != null && !requestLine.isEmpty()) {
                System.out.println("Request: " + requestLine);

                StringTokenizer tokenizer = new StringTokenizer(requestLine);
                if (tokenizer.countTokens() >= 2) {
                    String method = tokenizer.nextToken().trim();
                    String resource = tokenizer.nextToken().trim();

                    System.out.println("Method: " + method);
                    System.out.println("Resource: " + resource);

                    if (resource.startsWith("/api")) {
                        APIHandler.handleAPIRequest(method, resource, out);
                    } else if (resource.startsWith("/message")) {
                        MessageHandler.handleMessageRequest(resource, out);
                    } else {
                        StaticFileHandler.handleStaticFile(resource, out);
                    }
                } else {
                    out.println("HTTP/1.1 400 Bad Request");
                    out.println("Content-Type: text/html");
                    out.println();
                    out.println("<h1>400 Bad Request</h1>");
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
