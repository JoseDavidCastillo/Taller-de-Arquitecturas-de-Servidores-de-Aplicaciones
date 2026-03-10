# MicroSpringBoot Framework

Un mini-framework IoC (Inversion of Control) en Java que implementa capacidades reflexivas para descubrir y cargar controladores automáticamente desde el classpath.

## Características

- **Descubrimiento Automático de Componentes**: Escanea el classpath en tiempo de ejecución para encontrar clases anotadas con `@RestController`
- **Mapeo de Rutas**: Registra automáticamente métodos anotados con `@GetMapping` como rutas HTTP
- **Inyección de Parámetros**: Soporta `@RequestParam` con valores por defecto
- **Servidor Web HTTP**: Atiende solicitudes en múltiples puertos (no concurrente pero funcional)
- **Servicio de Archivos Estáticos**: Entrega HTML, CSS, imágenes PNG/JPEG, y otros recursos
- **Reflexión en Java**: Usa `Java Reflection API` para instantiar controladores y invocar métodos

## Estructura del Proyecto

```
Microweb/
├── src/
│   ├── main/
│   │   ├── java/edu/arep/
│   │   │   ├── App.java (Punto único de entrada)
│   │   │   ├── annotations/
│   │   │   │   ├── RestController.java
│   │   │   │   ├── GetMapping.java
│   │   │   │   └── RequestParam.java
│   │   │   ├── controllers/
│   │   │   │   ├── HelloController.java
│   │   │   │   └── GreetingController.java
│   │   │   └── framework/
│   │   │       ├── HttpServer.java
│   │   │       ├── WebFramework.java
│   │   │       ├── ControllerScanner.java
│   │   │       ├── Request.java
│   │   │       ├── Response.java
│   │   │       └── Route.java
│   │   └── resources/webroot/
│   │       └── index.html
│   └── test/ (tests)
└── pom.xml
```

## Cómo Usar

### 1. Compilar el Proyecto

```bash
cd Microweb
mvn clean compile
```

### 2. Ejecutar el Servidor

```bash
mvn package -DskipTests
java -cp target/classes edu.arep.App
```

El servidor escuchará en `http://localhost:8080`

### 3. Crear un Nuevo Controlador

```java
package edu.arep.controllers;

import edu.arep.annotations.*;

@RestController
public class MiControlador {
    
    @GetMapping("/miendpoint")
    public String saludo() {
        return "Hola Mundo!";
    }
    
    @GetMapping("/saludar")
    public String saludarUsuario(@RequestParam(value = "nombre", defaultValue = "Usuario") String nombre) {
        return "Hola, " + nombre + "!";
    }
}
```

El framework detectará automáticamente esta clase y registrará las rutas:
- `GET /miendpoint` → responde "Hola Mundo!"
- `GET /saludar?nombre=Juan` → responde "Hola, Juan!"
- `GET /saludar` → responde "Hola, Usuario!" (usando defaultValue)

## Anotaciones Disponibles

### @RestController
Marca una clase como componente controlador que será escaneado automáticamente.

```java
@RestController
public class MiControlador {
    // ...
}
```

### @GetMapping
Mapea un método a una ruta HTTP GET. El valor especifica la ruta.

```java
@GetMapping("/ruta")
public String metodo() {
    return "respuesta";
}
```

### @RequestParam
Inyecta parámetros de query string en métodos controladores.

```java
@GetMapping("/parametro")
public String metodo(@RequestParam(value = "param", defaultValue = "default") String param) {
    return param;
}
```

## Ejemplos de Uso

### Ejemplo 1: Endpoint Simple
```
GET /hello
Respuesta: <h1>Hello World!</h1>
```

### Ejemplo 2: Endpoint con Parámetro
```
GET /greeting?name=Juan
Respuesta: Hola Juan

GET /greeting
Respuesta: Hola World (usa defaultValue)
```

### Ejemplo 3: Página de Inicio
```
GET /
Respuesta: Página HTML con documentación del framework
```

## Componentes Principales

### ControllerScanner
Realiza el escaneo del classpath para encontrar clases con `@RestController` e invoca métodos mediante reflexión. Características principales:

- Escanea directorios del classpath
- Carga clases de forma segura (ignora clases que no se pueden cargar)
- Inyecta parámetros en métodos basándose en anotaciones
- Soporta valores por defecto en parámetros

### HttpServer
Servidor HTTP que atiende conexiones socket. Características:

- Escucha en un puerto específico
- Parsea solicitudes HTTP
- Enruta a controladores o archivos estáticos
- Soporta múltiples tipos MIME (HTML, CSS, JS, PNG, JPEG, etc.)

### WebFramework
Facade que centraliza la configuración del framework. Métodos principales:

- `staticfiles(String folder)`: Define ubicación de archivos estáticos
- `loadControllers()`: Escanea y carga todos los controladores
- `start(int port)`: Inicia el servidor HTTP

## Limitaciones Actuales

- No soporta concurrencia (procesa una solicitud a la vez)
- Solo soporta métodos GET (sin POST, PUT, DELETE)
- Solo retorna String (no JSON automático)
- No soporta validación de parámetros

## Tecnologías Usadas

- Java 14
- Maven
- Socket API de Java
- Java Reflection API
- Anotaciones (Annotations)

## Autor

Taller de Arquitecturas de Servidores de Aplicaciones
