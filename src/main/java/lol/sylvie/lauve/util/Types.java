package lol.sylvie.lauve.util;

import com.google.gson.JsonPrimitive;

public class Types {
    public static String asString(Object object) {
        return String.valueOf(object);
    }

    public static boolean asBool(Object object) {
        if (object instanceof Boolean bool) {
            return bool;
        }

        if (object instanceof String string) {
            return Boolean.parseBoolean(string);
        }

        if (object instanceof Number number) {
            return number.intValue() != 0;
        }

        return object != null;
    }

    public static double asNumber(Object object) {
        if (object instanceof Number number) return number.doubleValue();

        if (object instanceof String string) {
            return Double.parseDouble(string);
        }

        if (object instanceof Boolean bool) {
            return bool ? 1 : 0;
        }

        return 0d;
    }

    public static Object closestJava(JsonPrimitive element) {
        if (element.isBoolean()) return element.getAsBoolean();
        if (element.isNumber()) return element.getAsDouble();
        return element.getAsString();
    }
}
