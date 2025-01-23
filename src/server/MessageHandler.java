package src.server;

import java.io.PrintWriter;

public class MessageHandler {
    public static void handleMessageRequest(String resource, PrintWriter out) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/plain");
        out.println();
        out.println("Message request received for resource: " + resource);
    }
}
