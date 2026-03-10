package edu.arep.framework;

import java.util.HashMap;
import java.util.Map;

public class WebFramework {

    private static final Map<String, Route> routes = new HashMap<>();
    private static String staticFilesLocation = "";
    private static HttpServer serverInstance;

    public static void get(String path, Route handler) {
        routes.put(path, handler);
        System.out.println("[Framework] Registered GET route: " + path);
    }

    public static void staticfiles(String folder) {
        staticFilesLocation = folder;
        System.out.println("[Framework] Static files location: " + folder);
    }

    public static void start(int port) throws Exception {
        serverInstance = new HttpServer(port, routes, staticFilesLocation);
        serverInstance.start();
    }

    public static void loadControllers() throws Exception {
        System.out.println("[Framework] Scanning for @RestController classes...");
        ControllerScanner.scanAndRegisterControllers();
    }

    public static Map<String, Route> getRoutes() {
        return routes;
    }

    public static String getStaticFilesLocation() {
        return staticFilesLocation;
    }
}