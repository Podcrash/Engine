package com.podcrash.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//TODO: Tree caching
public final class ReflectionUtil {
    public static String nmsPrefix;

    public static void initiate() {
        Server server = Bukkit.getServer();
        try {
            Method getHandle = server.getClass().getDeclaredMethod("getHandle");
            Object handle = getHandle.invoke(server);
            Class dedicatedListClass = handle.getClass();
            String[] split = dedicatedListClass.getName().split("\\.");
            nmsPrefix = "net.minecraft.server." + split[3] + '.';
            System.out.println("NMS VER: " + nmsPrefix);
            //v1_8_R3

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Useful for methods that dont have arguments.
     * @param object
     * @param methodName
     * @param outputClass
     * @param <T>
     * @param <V>
     * @return
     */
    public static <T, V> V runNMSMethod(T object, String className, String methodName, Class<V> outputClass) {
        Class<?> clazz = null;
        try {
            String name = !className.contains(nmsPrefix) ? nmsPrefix + className : className;
            //PodcrashSpigot.debugLog("reflecting on " + name);
            clazz = Class.forName(name);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("clazz must be something!");
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName);
            boolean wasAccessible = false;
            if (!method.isAccessible()) {
                method.setAccessible(true);
                wasAccessible = true;
            }
            V output = (V) method.invoke(object);
            if (wasAccessible)
                method.setAccessible(false);
            return output;
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == Object.class) {
                e.printStackTrace();
                return null;
            }
            return runNMSMethod(object, superClass.getName(), methodName, outputClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    public static class MethodReflector<T> {
        private final Method method;

        public MethodReflector(Method method) {
            this.method = method;
        }

        public void invoke(Object... params) {
            try {
                this.method.invoke(null, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

 */
    public static <T, V> V runNMSMethod(T object, String className, String methodName, Class<V> outputClass, Class[] paramClasses, Object... parameters) {
        if (parameters.length == 0)
            return runNMSMethod(object, className, methodName, outputClass);
        Class<?> clazz;
        try {
            String name = !className.contains(nmsPrefix) ? nmsPrefix + className : className;
            clazz = Class.forName(name);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("clazz must be something!");
        }
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramClasses);
            boolean wasAccessible = false;
            if (!method.isAccessible()) {
                method.setAccessible(true);
                wasAccessible = true;
            }
            V output = (V) method.invoke(object, parameters);
            if (wasAccessible)
                method.setAccessible(false);
            return output;
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass == Object.class) {
                e.printStackTrace();
                return null;
            }
            return runNMSMethod(object, superClass.getName(), methodName, outputClass, paramClasses, parameters);
        } catch (IllegalArgumentException| InvocationTargetException| IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T constructor(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T constructor(Class<T> clazz, Class<?>[] paramTypes, Object... objects) {
        try {
            Constructor<T> constructor = clazz.getConstructor(paramTypes);
            return constructor.newInstance(objects);
        }catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
