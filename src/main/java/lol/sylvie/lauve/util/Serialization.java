package lol.sylvie.lauve.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.UUID;

public class Serialization {
    public static Object toClosestJava(JsonPrimitive element) {
        if (element.isBoolean()) return element.getAsBoolean();
        if (element.isNumber()) return element.getAsDouble();
        return element.getAsString();
    }

    public static UUID uuidOf(JsonObject object, String key) {
        String uuidAsString = object.get(key).getAsString();
        return UUID.fromString(uuidAsString);
    }
}
