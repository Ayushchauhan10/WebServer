package src.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler {
    private static final String BASE_DIR = "src/files";

    private static final Map<String, String> MIME_TYPES = new HashMap<>();
    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("txt", "text/plain");
    }

    public static void handleStaticFile(String resource, PrintWriter out) {
        if (resource.equals("/")) resource = "/index.html";

        String filePath = BASE_DIR + resource;
        File file = new File(filePath);

        try {
            if (file.exists()) {
                String extension = getFileExtension(file);
                String contentType = MIME_TYPES.getOrDefault(extension, "application/octet-stream");

                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + contentType);
                out.println();

                if (contentType.equals("application/octet-stream") || extension.equals("jpg") || extension.equals("png")) {
                    sendBinaryFile(file, out);
                } else {
                    sendTextFile(file, out);
                }

                System.out.println("Served static file: " + filePath);
            } else {
                send404(out, filePath);
            }
        } catch (IOException e) {
            System.err.println("Error serving static file: " + e.getMessage());
            send500(out);
        }
    }

    private static void sendTextFile(File file, PrintWriter out) throws IOException {
        Files.lines(file.toPath()).forEach(out::println);
    }

    private static void sendBinaryFile(File file, PrintWriter out) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(new String(buffer, 0, bytesRead));
            }
        }
    }

    private static void send404(PrintWriter out, String filePath) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>404 Not Found</h1>");
        System.out.println("Static file not found: " + filePath);
    }

    private static void send500(PrintWriter out) {
        out.println("HTTP/1.1 500 Internal Server Error");
        out.println("Content-Type: text/html");
        out.println();
        out.println("<h1>500 Internal Server Error</h1>");
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex == -1) ? "" : name.substring(dotIndex + 1);
    }
}
