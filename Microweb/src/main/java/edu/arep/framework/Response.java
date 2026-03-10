package edu.arep.framework;

public class Response {

    private int statusCode = 200;
    private String contentType = "text/plain";

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * Builds an HTTP response string for text/JSON body content.
     */
    public String build(String body) {
        return "HTTP/1.1 " + statusCode + " " + statusText() + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.getBytes().length + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;
    }

    /**
     * Builds an HTTP response for raw bytes (static files).
     */
    public String buildHeader(int contentLength) {
        return "HTTP/1.1 " + statusCode + " " + statusText() + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + contentLength + "\r\n"
                + "Connection: close\r\n"
                + "\r\n";
    }

    public static String notFound() {
        String body = "404 Not Found";
        return "HTTP/1.1 404 Not Found\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;
    }

    private String statusText() {
        return switch (statusCode) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}