package lol.sylvie.lauve.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflection {
    public static String getParentPackage(Class<?> clazz) {
        String name = clazz.getPackageName();
        String[] split = name.split("\\.");
        return split[split.length - 1]; // Last element is class name, second to last is package name
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }
}
