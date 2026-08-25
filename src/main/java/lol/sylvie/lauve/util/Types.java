package lol.sylvie.lauve.util;

public class Types {
    public static String string(Object object) {
        return String.valueOf(object);
    }

    public static boolean bool(Object object) {
        if (object instanceof Boolean bool) {
            return bool;
        }

        if (object instanceof String string) {
            return Boolean.valueOf(string);
        }

        if (object instanceof Number number) {
            return number.intValue() != 0;
        }

        return object != null;
    }
}
