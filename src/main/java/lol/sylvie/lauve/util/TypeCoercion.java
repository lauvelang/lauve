package lol.sylvie.lauve.util;

public class TypeCoercion {
    public static boolean canBeNumber(Object value) {
        if (value instanceof Number) return true;

        if (value instanceof String str) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }

        return value instanceof Boolean;
    }

    public static double toNumber(Object value) {
        if (value instanceof Number number) return number.doubleValue();
        if (value instanceof String string) return Double.parseDouble(string);
        if (value instanceof Boolean bool) return bool ? 1 : 0;
        return 0d;
    }

    public static String toString(Object value) {
        if (value instanceof String string) return string;
        if (value instanceof Number number) return String.valueOf(number);
        return value.toString();
    }

    public static boolean toBool(Object value) {
        if (value instanceof Boolean bool) return bool;
        if (TypeCoercion.canBeNumber(value)) return TypeCoercion.toNumber(value) != 0;
        if (value instanceof String string) return Boolean.parseBoolean(string);
        return value != null;
    }
}
