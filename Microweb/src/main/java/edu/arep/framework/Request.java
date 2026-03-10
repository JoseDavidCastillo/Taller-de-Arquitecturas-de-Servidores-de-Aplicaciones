package edu.arep.framework;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;

    public Request(String rawRequest) {
        String firstLine = rawRequest.split("\r\n")[0];
        String[] parts = firstLine.split(" ");

        this.method = parts[0];
        String fullPath = parts.length > 1 ? parts[1] : "/";

        // Split path and query string
        if (fullPath.contains("?")) {
            String[] split = fullPath.split("\\?", 2);
            this.path = split[0];
            this.queryParams = parseQueryParams(split[1]);
        } else {
            this.path = fullPath;
            this.queryParams = new HashMap<>();
        }
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                params.put(kv[0], "");
            }
        }
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getValues(String key) {
        return queryParams.getOrDefault(key, "");
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}