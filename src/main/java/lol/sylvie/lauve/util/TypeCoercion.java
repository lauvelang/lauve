package lol.sylvie.lauve.util;

import net.minecraft.network.chat.PlayerChatMessage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class TypeCoercion {
    // Getters
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

    public static boolean fuzzyEquals(double x, double y) {
        return Math.abs(x - y) < 1E-6;
    }

    // Setters
    public static void setField(Object object, Field field, Object value) throws IllegalAccessException {
        if (field.getType().equals(PlayerChatMessage.class)) {
            PlayerChatMessage previous = (PlayerChatMessage) field.get(object);
            String text = toString(value);
            field.set(object, PlayerChatMessage.system(text));
            return;
        }

        field.set(object, value);
    }
}
