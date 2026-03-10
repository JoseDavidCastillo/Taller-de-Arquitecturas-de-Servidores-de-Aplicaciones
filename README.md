# MicroSpringBoot — IoC Framework with Reflection-based Component Discovery

A lightweight Java IoC (Inversion of Control) framework built on raw sockets that enables **automatic discovery and loading of REST controllers** using **Java reflection**. Instead of manually registering routes, developers simply annotate POJOs with `@RestController` and define methods with `@GetMapping` — the framework handles the rest.

## Features

- 🔍 **Automatic Component Discovery** — Scans the classpath at runtime for classes annotated with `@RestController`
- 🔧 **Reflection-based Instantiation** — Uses Java reflection to load and instantiate controller classes dynamically
- 🛣️ **Route Mapping** — Automatically registers `@GetMapping` methods as HTTP endpoints
- 📝 **Parameter Injection** — Supports `@RequestParam` with default values
- 📂 **Static File Serving** — Delivers HTML, CSS, images, and other resources
- 🚀 **Single Entry Point** — Single `main()` method with automatic framework initialization

## Author

Jose David Castillo Rodriguez

## Table of Contents

- [Project Description](#project-description)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [Usage / Developer API](#usage--developer-api)
- [Test Examples](#test-examples)

---

## Project Description

MicroSpringBoot is a lightweight HTTP server and IoC framework written in Java that demonstrates advanced reflection capabilities. The framework automatically scans the classpath for controller classes and registers their methods as HTTP endpoints without requiring any XML configuration or manual route registration.

Developers can build web applications by:

- Creating **POJO classes** annotated with `@RestController`
- Defining **HTTP endpoints** with `@GetMapping("/path")`
- Using **parameter injection** with `@RequestParam` for query parameters
- Serving **static files** from a configured directory
- Building and deploying using **Maven** with a **single main entry point**

This project demonstrates Java's reflective capabilities and the power of annotations in building flexible, convention-over-configuration frameworks.

---

## Architecture

The framework uses Java reflection to automatically discover and register controllers at runtime:

```
Application Startup
        │
        ├─▶ App.main()
        │    ├─ Configure static files location
        │    ├─ loadControllers() ──▶ ControllerScanner
        │    │    │
        │    │    ├─ Scan classpath for .class files
        │    │    ├─ Find classes with @RestController
        │    │    ├─ Create instances via reflection
        │    │    └─ Register @GetMapping methods as routes
        │    │
        │    └─ start(8080) ──▶ HttpServer begins listening
        │
HTTP Request Arrives
        │
        ├─▶ HttpServer.handleClient()
        │    ├─ Parse HTTP request
        │    ├─ Extract path & query parameters
        │    ├─ Look up route in WebFramework.routes
        │    │
        │    ├─ If route found:
        │    │  └─ Invoke controller method with injected parameters
        │    │
        │    └─ If not found:
        │       └─ Try to serve static file from webroot
        │
        └─▶ Response sent to client
```

### Component Responsibilities

| Component | Role |
|---|---|
| `RestController.java` | Annotation — marks classes as IoC components |
| `GetMapping.java` | Annotation — marks methods as HTTP endpoints |
| `RequestParam.java` | Annotation — enables parameter injection |
| `ControllerScanner.java` | **NEW** — Scans classpath, discovers components, instantiates via reflection |
| `Request.java` | Parses raw HTTP requests; exposes query parameters |
| `Response.java` | Constructs valid HTTP/1.1 responses |
| `WebFramework.java` | Static API surface: `get()`, `staticfiles()`, `loadControllers()`, `start()` |
| `HttpServer.java` | Socket listener and request dispatcher |
| `Route.java` | Functional interface for request handlers |
| `App.java` | Single entry point; initializes framework and starts server |

---

## Project Structure

```
Taller-de-Arquitecturas-de-Servidores-de-Aplicaciones/
├── README.md (this file)
├── RESUMEN_IMPLEMENTACION.md (detailed implementation summary)
└── Microweb/
    ├── pom.xml
    ├── FRAMEWORK_GUIDE.md
    └── src/
        ├── main/
        │   ├── java/
        │   │   └── edu/
        │   │       └── arep/
        │   │           ├── App.java (single entry point)
        │   │           ├── annotations/
        │   │           │   ├── RestController.java
        │   │           │   ├── GetMapping.java
        │   │           │   └── RequestParam.java
        │   │           ├── controllers/
        │   │           │   ├── HelloController.java
        │   │           │   └── GreetingController.java
        │   │           └── framework/
        │   │               ├── ControllerScanner.java (NEW - reflection scanner)
        │   │               ├── HttpServer.java
        │   │               ├── WebFramework.java
        │   │               ├── Request.java
        │   │               ├── Response.java
        │   │               └── Route.java
        │   └── resources/
        │       └── webroot/
        │           └── index.html
        └── test/
            └── java/
                └── edu/arep/AppTest.java
```

---

## How to Run

### Prerequisites

- Java 14 or higher
- Maven 3.6 or higher

### Build and Run

1. Navigate to the project directory:
```bash
cd Microweb
```

2. Build the project:
```bash
mvn clean package -DskipTests
```

3. Run the application:
```bash
java -cp target/classes edu.arep.App
```

4. The server will start on port `8080`:
```
[Framework] Static files location: /webroot
[Framework] Scanning for @RestController classes...
[Scanner] Found controller: edu.arep.controllers.GreetingController
[Framework] Registered GET route: /greeting
[Scanner] Found controller: edu.arep.controllers.HelloController
[Framework] Registered GET route: /hello
[Framework] Registered GET route: /
[Server] Listening on port 8080
```

5. Access the framework:
   - Home page: http://localhost:8080/
   - Hello endpoint: http://localhost:8080/hello
   - Greeting with parameter: http://localhost:8080/greeting?name=YourName
   - Greeting with default: http://localhost:8080/greeting

---

## Usage / Developer API

### Creating a REST Controller

Instead of manually registering routes, developers simply create POJO classes with annotations:

```java
package edu.arep.controllers;

import edu.arep.annotations.*;

@RestController
public class GreetingController {
    
    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(value = "name", defaultValue = "World") String name
    ) {
        return "Hola " + name;
    }
}
```

The framework will:
1. **Auto-discover** this class via `@RestController`
2. **Auto-register** the `/greeting` endpoint via `@GetMapping`
3. **Auto-inject** the `name` parameter via `@RequestParam`
4. **Auto-instantiate** the controller and invoke the method

### App.java — Single Entry Point

```java
package edu.arep;

import static edu.arep.framework.WebFramework.*;

public class App {
    public static void main(String[] args) throws Exception {
        staticfiles("/webroot");     // Configure static files location
        loadControllers();           // Auto-discover and register controllers
        start(8080);                 // Start HTTP server
    }
}
```

### API Reference

#### WebFramework Methods

| Method | Signature | Description |
|---|---|---|
| `staticfiles()` | `staticfiles(String folder)` | Sets the static files classpath folder (e.g., `/webroot`) |
| `loadControllers()` | `loadControllers()` | Scans classpath for `@RestController` classes and registers routes |
| `start()` | `start(int port)` | Starts the HTTP server on the specified port |
| `get()` | `get(String path, Route handler)` | Manual route registration (optional, for lambda-based handlers) |

#### Request Object

| Method | Description |
|---|---|
| `getPath()` | Returns the request path (e.g., `/greeting`) |
| `getMethod()` | Returns the HTTP method (e.g., `GET`) |
| `getValues(String key)` | Returns the value of a query parameter |
| `getQueryParams()` | Returns all query parameters as a Map |

#### Annotations

| Annotation | Target | Purpose |
|---|---|---|
| `@RestController` | Class | Marks a class as an IoC component for auto-discovery |
| `@GetMapping(path)` | Method | Maps a method to an HTTP GET endpoint |
| `@RequestParam(value, defaultValue)` | Parameter | Injects query parameters into method arguments |

---

## Test Examples

Once the server is running on `http://localhost:8080`, try these endpoints:

### 1. Home Page
```bash
curl http://localhost:8080/
```
**Response:** HTML page with framework documentation and endpoint listing

### 2. Simple Hello Endpoint
```bash
curl http://localhost:8080/hello
```
**Response:** `<h1>Hello World!</h1>`

### 3. Greeting with Parameter
```bash
curl http://localhost:8080/greeting?name=MicroSpringBoot
```
**Response:** `Hola MicroSpringBoot`

### 4. Greeting with Default Parameter
```bash
curl http://localhost:8080/greeting
```
**Response:** `Hola World` (uses default value from `@RequestParam`)

### 5. Multiple Parameters
```bash
curl http://localhost:8080/greeting?name=Juan&age=25
```
**Response:** `Hola Juan` (only injected parameter is used; others are ignored)

---

## Key Improvements Over Lambda-Based Approach

| Aspect | Lambda-Based | Annotation-Based (MicroSpringBoot) |
|---|---|---|
| **Route Registration** | Manual via `get()` calls | Automatic via `@RestController` scan |
| **Configuration** | Programmatic in `main()` | Declarative via annotations |
| **Scalability** | Routes must be hardcoded | New controllers auto-discovered |
| **Separation of Concerns** | Mix of setup and logic | Clean separation of concerns |
| **Framework Principles** | Service Locator pattern | Inversion of Control (IoC) |
| **Reflection Usage** | Minimal | Extensive (component discovery) |

---

## Learning Outcomes

This project demonstrates:

- ✅ **Java Reflection API** — Dynamic class loading, instantiation, method invocation
- ✅ **Annotations** — Custom annotations and runtime retention
- ✅ **IoC Container** — Automatic component discovery and lifecycle management
- ✅ **HTTP Protocol** — Raw socket server with HTTP/1.1 compliance
- ✅ **Design Patterns** — Inversion of Control, Dependency Injection, Facade
- ✅ **Maven Build System** — Project structure, compilation, packaging
- ✅ **Java Generics and Collections** — HashMap for route storage
