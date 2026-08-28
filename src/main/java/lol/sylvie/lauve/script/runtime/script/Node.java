package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.operation.Operations;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.Types;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;

public record Node(
        String rid,
        Operation operation,
        HashMap<String, Argument> args,
        @Nullable String parent,
        @Nullable String next) {
    public static Node load(String rid, JsonObject object) {
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
                String stringRid = primitive.getAsString();
                argument = new Argument(stringRid);
            }

            args.put(argKey, argument);
        }

        JsonElement parentElement = object.get("parent");
        JsonElement nextElement = object.get("next");

        return new Node(rid,
                operation,
                args,
                parentElement == null || parentElement.isJsonNull() ? null : parentElement.getAsString(),
                nextElement == null || nextElement.isJsonNull() ? null : nextElement.getAsString());
    }
}
