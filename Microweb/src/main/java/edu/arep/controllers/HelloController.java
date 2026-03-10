package edu.arep.controllers;

import edu.arep.annotations.*;

@RestController
public class HelloController {

    @GetMapping("/")
    public String index() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>MicroSpringBoot Framework</title><style>body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; }h1 { color: #333; }h2 { color: #666; }p { color: #555; line-height: 1.6; }.endpoint { background: #f5f5f5; padding: 10px; margin: 10px 0; border-left: 4px solid #0066cc; }a { text-decoration: none; color: #0066cc; font-weight: bold; }a:hover { text-decoration: underline; }</style></head><body><h1>MicroSpringBoot Framework is Running!</h1><h2>IoC Container with Reflective Component Loading</h2><p>This server automatically scans for @RestController classes and registers their @GetMapping methods.</p><h3>Available Endpoints:</h3><div class=\"endpoint\"><p><strong>GET /</strong> - Home page (this page)</p></div><div class=\"endpoint\"><p><strong>GET /hello</strong> - Simple Hello World endpoint</p><p><a href=\"/hello\">Visit /hello</a></p></div><div class=\"endpoint\"><p><strong>GET /greeting?name=&lt;name&gt;</strong> - Greeting with parameter</p><p><a href=\"/greeting\">Visit /greeting (uses default value)</a></p><p><a href=\"/greeting?name=MicroSpringBoot\">Visit /greeting?name=MicroSpringBoot</a></p></div><h3>Key Features:</h3><ul><li><strong>Automatic Component Discovery</strong> - Scans classpath for @RestController classes</li><li><strong>Reflective Instantiation</strong> - Creates controller instances using Java reflection</li><li><strong>Route Mapping</strong> - Registers @GetMapping methods as URL routes</li><li><strong>Parameter Injection</strong> - Supports @RequestParam with default values</li><li><strong>Static File Serving</strong> - Delivers HTML, CSS, PNG, JPEG, and other resources</li></ul><h3>How It Works:</h3><p>1. App.java loads the framework and calls loadControllers()</p><p>2. ControllerScanner finds all classes annotated with @RestController</p><p>3. For each controller, it scans for @GetMapping methods</p><p>4. Methods are registered as HTTP routes with parameter injection support</p></body></html>";
    }

    @GetMapping("/hello")
    public String hello() {
        return "<h1>Hello World!</h1>";
    }
}

