package lol.sylvie.lauve.util;

public class Reflection {
    public static String getParentPackage(Class<?> clazz) {
        String name = clazz.getPackageName();
        String[] split = name.split("\\.");
        return split[split.length - 2]; // Last element is class name, second to last is package name
    }
}
