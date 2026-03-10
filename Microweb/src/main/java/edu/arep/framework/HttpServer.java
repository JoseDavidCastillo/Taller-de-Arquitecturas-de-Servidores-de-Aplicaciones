package edu.arep.framework;

import java.io.*;
import java.net.*;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final Map<String, Route> routes;
    private final String staticFilesLocation;

    public HttpServer(int port, Map<String, Route> routes, String staticFilesLocation) {
        this.port = port;
        this.routes = routes;
        this.staticFilesLocation = staticFilesLocation;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("[Server] Listening on port " + port);

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleClient(clientSocket);
            } catch (Exception e) {
                System.err.println("[Server] Error handling client: " + e.getMessage());
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        StringBuilder rawRequest = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            rawRequest.append(line).append("\r\n");
        }

        if (rawRequest.length() == 0) return;

        Request req = new Request(rawRequest.toString());
        Response res = new Response();

        String path = req.getPath();
        System.out.println("[Server] Request: " + req.getMethod() + " " + path);

        // Check if path is a registered route
        Route handler = routes.get(path);
        if (handler != null) {
            String body = handler.handle(req, res);
            res.setContentType("text/html; charset=UTF-8");
            out.write(res.build(body).getBytes());
        } else {
            // Try to serve as static file
            serveStaticFile(path, res, out);
        }

        out.flush();
    }

    private void serveStaticFile(String urlPath, Response res, OutputStream out) throws IOException {
        if (urlPath.equals("/")) {
            urlPath = "/index.html";
        }

        String resourcePath = staticFilesLocation + urlPath;
        InputStream fileStream = getClass().getResourceAsStream(resourcePath);

        if (fileStream == null) {
            out.write(Response.notFound().getBytes());
            return;
        }

        byte[] fileBytes = fileStream.readAllBytes();
        res.setContentType(getMimeType(urlPath));
        res.setStatus(200);

        out.write(res.buildHeader(fileBytes.length).getBytes());
        out.write(fileBytes);
    }

    private String getMimeType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "text/plain";
    }
}
