package edu.arep;

import static edu.arep.framework.WebFramework.staticfiles;
import static edu.arep.framework.WebFramework.loadControllers;
import static edu.arep.framework.WebFramework.start;

public class App {
    public static void main(String[] args) throws Exception {
        staticfiles("/webroot");
        loadControllers();
        start(8080);
    }
}

