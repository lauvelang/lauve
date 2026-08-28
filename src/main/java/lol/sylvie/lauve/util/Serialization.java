package lol.sylvie.lauve.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.UUID;

public class Serialization {
    public static UUID uuidOf(JsonObject object, String key) {
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull()) return null;
        String uuidAsString = element.getAsString();
        return UUID.fromString(uuidAsString);
    }
}
