package lol.sylvie.lauve.util;

import com.google.gson.JsonPrimitive;

public class Types {
    public static Object closestJava(JsonPrimitive element) {
        if (element.isBoolean()) return element.getAsBoolean();
        if (element.isNumber()) return element.getAsDouble();
        return element.getAsString();
    }
}
