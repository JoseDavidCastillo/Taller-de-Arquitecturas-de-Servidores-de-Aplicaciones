package edu.arep.framework;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

/**
 * Bootstrap del framework.
 * Escanea el classpath buscando clases con @RestController
 * y registra sus métodos @GetMapping como rutas.
 */
public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {
        // Escanear todo el classpath automáticamente
        List<Class<?>> controllers = scanControllers();

        for (Class<?> controller : controllers) {
            registerController(controller);
        }

        WebFramework.staticfiles("/webroot");
        WebFramework.start(8080);
    }

    /**
     * Escanea el classpath buscando clases anotadas con @RestController
     */
    private static List<Class<?>> scanControllers() throws Exception {
        List<Class<?>> found = new ArrayList<>();
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(File.pathSeparator);

        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                scanDirectory(file, file, found);
            }
        }

        System.out.println("[MicroSpringBoot] Controllers found: " + found.size());
        return found;
    }

    private static void scanDirectory(File root, File current, List<Class<?>> found) {
        for (File file : Objects.requireNonNull(current.listFiles())) {
            if (file.isDirectory()) {
                scanDirectory(root, file, found);
            } else if (file.getName().endsWith(".class")) {
                String className = root.toURI().relativize(file.toURI())
                        .getPath()
                        .replace("/", ".")
                        .replace(".class", "");
                try {
                    Class<?> cls = Class.forName(className);
                    if (cls.isAnnotationPresent(RestController.class)) {
                        found.add(cls);
                        System.out.println("[MicroSpringBoot] Loaded controller: " + className);
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Registra todos los métodos @GetMapping de un controller como rutas
     */
    private static void registerController(Class<?> controllerClass) throws Exception {
        Object instance = controllerClass.getDeclaredConstructor().newInstance();

        for (Method method : controllerClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(GetMapping.class)) continue;

            String path = method.getAnnotation(GetMapping.class).value();

            WebFramework.get(path, (req, res) -> {
                try {
                    Object[] params = resolveParams(method, req);
                    return (String) method.invoke(instance, params);
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            });

            System.out.println("[MicroSpringBoot] Mapped GET " + path
                    + " -> " + controllerClass.getSimpleName() + "." + method.getName() + "()");
        }
    }

    /**
     * Resuelve los parámetros del método usando @RequestParam via reflexión
     */
    private static Object[] resolveParams(Method method, edu.arep.framework.Request req) {
        Parameter[] parameters = method.getParameters();
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam annotation = parameters[i].getAnnotation(RequestParam.class);
                String val = req.getValues(annotation.value());
                values[i] = val.isEmpty() ? annotation.defaultValue() : val;
            } else {
                values[i] = null;
            }
        }

        return values;
    }
}
