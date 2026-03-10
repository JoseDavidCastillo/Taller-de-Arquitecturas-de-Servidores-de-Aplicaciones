package edu.arep.framework;

@FunctionalInterface
public interface Route {
    String handle(Request req, Response res);
}