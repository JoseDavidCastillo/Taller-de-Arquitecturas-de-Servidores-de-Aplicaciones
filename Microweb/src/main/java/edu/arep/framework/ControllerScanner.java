package edu.arep.framework;

import edu.arep.annotations.GetMapping;
import edu.arep.annotations.RequestParam;
import edu.arep.annotations.RestController;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerScanner {

    /**
     * Scans the classpath for classes annotated with @RestController
     * and registers their @GetMapping methods as routes.
     */
    public static void scanAndRegisterControllers() throws Exception {
        List<Class<?>> controllers = findControllers();
        
        for (Class<?> controllerClass : controllers) {
            System.out.println("[Scanner] Found controller: " + controllerClass.getName());
            registerControllerRoutes(controllerClass);
        }
    }

    /**
     * Finds all classes in the classpath annotated with @RestController
     */
    private static List<Class<?>> findControllers() throws Exception {
        List<Class<?>> controllers = new ArrayList<>();
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(File.pathSeparator);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                scanDirectory(file, "", classLoader, controllers);
            }
        }

        return controllers;
    }

    /**
     * Recursively scans directories for .class files
     */
    private static void scanDirectory(File directory, String packageName, ClassLoader classLoader, List<Class<?>> controllers) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                scanDirectory(file, newPackage, classLoader, controllers);
            } else if (file.getName().endsWith(".class")) {
                String className = file.getName().substring(0, file.getName().length() - 6);
                String fullClassName = packageName.isEmpty() ? className : packageName + "." + className;

                try {
                    Class<?> clazz = Class.forName(fullClassName, false, classLoader);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        controllers.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Silently skip classes that can't be loaded
                }
            }
        }
    }

    /**
     * Registers all @GetMapping methods of a controller class as routes
     */
    private static void registerControllerRoutes(Class<?> controllerClass) throws Exception {
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
        Method[] methods = controllerClass.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String path = mapping.value();
                
                method.setAccessible(true);

                // Create a Route handler that invokes the method with proper parameter handling
                Route handler = (req, res) -> {
                    try {
                        return invokeControllerMethod(controllerInstance, method, req);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "Error: " + e.getMessage();
                    }
                };

                WebFramework.get(path, handler);
                System.out.println("[Scanner] Registered route: " + path + " -> " + controllerClass.getSimpleName() + "." + method.getName());
            }
        }
    }

    /**
     * Invokes a controller method with proper parameter injection
     */
    private static String invokeControllerMethod(Object instance, Method method, Request req) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = param.getAnnotation(RequestParam.class);
                String paramName = !requestParam.value().isEmpty() ? requestParam.value() : param.getName();
                String value = req.getValues(paramName);

                if (value == null || value.isEmpty()) {
                    value = requestParam.defaultValue();
                }

                paramValues[i] = value;
            }
        }

        Object result = method.invoke(instance, paramValues);
        return result != null ? result.toString() : "";
    }
}
