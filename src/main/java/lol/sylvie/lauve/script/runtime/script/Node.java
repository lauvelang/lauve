package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.operation.Operations;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.Serialization;
import lol.sylvie.lauve.util.Types;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public record Node(
        UUID rid,
        Operation operation,
        HashMap<String, Argument> args,
        @Nullable UUID parent,
        @Nullable UUID next) {
    public static Node load(UUID rid, JsonObject object) {
        Id key = new Id(object.get("opcode").getAsString());
        Operation operation = Operations.get(key);
        if (operation == null) throw new RuntimeException("Operation not found: " + key);

        HashMap<String, Argument> args = new HashMap<>();
        JsonObject rawArgs = object.getAsJsonObject("args");
        for (String argKey : rawArgs.keySet()) {
            JsonArray array = rawArgs.get(argKey).getAsJsonArray();
            boolean computed = array.get(0).getAsBoolean();

            Argument argument;
            JsonPrimitive primitive = array.get(1).getAsJsonPrimitive();
            if (computed) {
                argument = new Argument(Types.closestJava(primitive));
            } else {
                String stringUuid = primitive.getAsString();
                UUID uuid = UUID.fromString(stringUuid);
                argument = new Argument(uuid);
            }

            args.put(argKey, argument);
        }

        return new Node(rid,
                operation,
                args,
                Serialization.uuidOf(object, "parent"),
                Serialization.uuidOf(object, "next"));
    }
}
