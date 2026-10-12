package lol.sylvie.lauve.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.UUID;

public class Serialization {
    public static Object closestJava(JsonPrimitive element) {
        if (element == null) return null;
        if (element.isBoolean()) return element.getAsBoolean();
        if (element.isNumber()) return element.getAsDouble();
        return element.getAsString();
    }

    public static JsonElement closestJson(Object object) {
        return switch (object) {
            case null -> null;
            case Boolean boolObject -> new JsonPrimitive(boolObject);
            case Number numberObject -> new JsonPrimitive(numberObject);
            default -> new JsonPrimitive(object.toString());
        };
    }
}
