package edu.arep;

import static edu.arep.framework.WebFramework.staticfiles;
import static edu.arep.framework.WebFramework.get;
import static edu.arep.framework.WebFramework.start;

public class App 
{
    public static void main( String[] args ) throws Exception {
        staticfiles("/webroot");

        get("/hello", (req, res) -> "Hello " + req.getValues("name"));

        get("/pi", (req, res) -> String.valueOf(Math.PI));

        start(8080);
    }
}
