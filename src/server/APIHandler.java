package src.server;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class APIHandler {
    private static final String DATA_FILE = "src/files/data.txt";

    public static void handleAPIRequest(String method, String resource, PrintWriter out) {
        synchronized (APIHandler.class) {
            try {
                switch (method) {
                    case "GET":
                        handleGet(out);
                        break;
                    case "POST":
                        handlePost(resource, out);
                        break;
                    case "PUT":
                        handlePut(resource, out);
                        break;
                    case "DELETE":
                        handleDelete(resource, out);
                        break;
                    default:
                        sendErrorResponse(out, "Unsupported HTTP method");
                }
            } catch (Exception e) {
                sendErrorResponse(out, "Internal Server Error: " + e.getMessage());
            }
        }
    }

    private static void handleGet(PrintWriter out) throws IOException {
        Map<String, String> data = readDataFile();
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(mapToJson(data));
        System.out.println("GET: Returned data");
    }

    private static void handlePost(String resource, PrintWriter out) throws IOException {
        String[] keyValue = parseKeyValue(resource, out);
        if (keyValue == null) return;

        Map<String, String> data = readDataFile();
        if (data.containsKey(keyValue[0])) {
            sendErrorResponse(out, "Key already exists");
        } else {
            data.put(keyValue[0], keyValue[1]);
            writeDataFile(data);
            sendSuccessResponse(out, "Entry added successfully");
            System.out.println("POST: Added key " + keyValue[0]);
        }
    }

    private static void handlePut(String resource, PrintWriter out) throws IOException {
        String[] keyValue = parseKeyValue(resource, out);
        if (keyValue == null) return;

        Map<String, String> data = readDataFile();
        if (!data.containsKey(keyValue[0])) {
            sendErrorResponse(out, "Key does not exist");
        } else {
            data.put(keyValue[0], keyValue[1]);
            writeDataFile(data);
            sendSuccessResponse(out, "Entry updated successfully");
            System.out.println("PUT: Updated key " + keyValue[0]);
        }
    }

    private static void handleDelete(String resource, PrintWriter out) throws IOException {
        String key = parseKey(resource, out);
        if (key == null) return;

        Map<String, String> data = readDataFile();
        if (!data.containsKey(key)) {
            sendErrorResponse(out, "Key does not exist");
        } else {
            data.remove(key);
            writeDataFile(data);
            sendSuccessResponse(out, "Entry deleted successfully");
            System.out.println("DELETE: Removed key " + key);
        }
    }

    private static Map<String, String> readDataFile() throws IOException {
        Map<String, String> data = new HashMap<>();
        if (!Files.exists(Paths.get(DATA_FILE))) {
            Files.createFile(Paths.get(DATA_FILE));
        }
        List<String> lines = Files.readAllLines(Paths.get(DATA_FILE));
        for (String line : lines) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                data.put(parts[0], parts[1]);
            }
        }
        return data;
    }

    private static void writeDataFile(Map<String, String> data) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATA_FILE))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        }
    }

    private static String[] parseKeyValue(String resource, PrintWriter out) {
        String[] parts = resource.split("\\?");
        if (parts.length < 2 || !parts[1].contains("=")) {
            sendErrorResponse(out, "Invalid format. Use /api?key=value");
            return null;
        }
        return parts[1].split("=", 2);
    }

    private static String parseKey(String resource, PrintWriter out) {
        String[] parts = resource.split("\\?");
        if (parts.length < 2) {
            sendErrorResponse(out, "Invalid format. Use /api?key");
            return null;
        }
        return parts[1];
    }

    private static String mapToJson(Map<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        if (json.length() > 1) json.setLength(json.length() - 1); // Remove trailing comma
        json.append("}");
        return json.toString();
    }

    private static void sendSuccessResponse(PrintWriter out, String message) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"message\":\"" + message + "\"}");
    }

    private static void sendErrorResponse(PrintWriter out, String message) {
        out.println("HTTP/1.1 400 Bad Request");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"error\":\"" + message + "\"}");
    }
}
