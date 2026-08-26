package lol.sylvie.lauve.script.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.script.input.Inputs;
import lol.sylvie.lauve.script.input.impl.engine.StaticInput;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.operation.Operations;
import lol.sylvie.lauve.script.runtime.node.InputNode;
import lol.sylvie.lauve.script.runtime.node.OperationNode;
import lol.sylvie.lauve.util.Constants;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.Serialization;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class Script {
    private final File file;

    private UUID entrypoint;
    private final HashMap<UUID, OperationNode> operationNodes = new HashMap<>();
    private final HashMap<UUID, InputNode> inputNodes = new HashMap<>();

    public InputNode loadInputNode(UUID rid, JsonObject root, JsonObject object) {
        Id key = new Id(object.get("incode").getAsString());
        Input input = Inputs.get(key);

        JsonObject rawArgs = object.getAsJsonObject("args");
        HashMap<String, InputNode> args = new HashMap<>();
        for (String argKey : rawArgs.keySet()) {
            JsonObject arg = rawArgs.getAsJsonObject(argKey);

            JsonElement value = arg.get("value");
            if (value != null) {
                // resolve to java object
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                Object staticValue = Serialization.toClosestJava(primitive);
                args.put(argKey, new InputNode(rid, new StaticInput(staticValue), null));
            } else {
                UUID reference = Serialization.uuidOf(arg, "reference");
                InputNode child = inputNodes.computeIfAbsent(reference, _ -> loadInputNode(reference, root, root.getAsJsonObject(reference.toString())));
                args.put(argKey, child);
            }
        }

        return new InputNode(rid, input, args);
    }

    public OperationNode loadOperationNode(UUID rid, JsonObject object) {
        Id key = new Id(object.get("opcode").getAsString());
        Operation operation = Operations.get(key);

        HashMap<String, InputNode> args = new HashMap<>();
        JsonObject rawArgs = object.getAsJsonObject("args");
        for (String argKey : rawArgs.keySet()) {
            String referenceString = rawArgs.get(argKey).getAsString();
            UUID reference = UUID.fromString(referenceString);

            args.put(argKey, inputNodes.get(reference));
        }

        return new OperationNode(rid,
                operation,
                args,
                Serialization.uuidOf(object, "parent"),
                Serialization.uuidOf(object, "next"));
    }

    public void load() {
        JsonObject root;
        try (FileReader reader = new FileReader(this.file)) {
            root = Constants.GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.entrypoint = Serialization.uuidOf(root, "entrypoint");

        // Input nodes
        JsonObject inputs = root.getAsJsonObject("inputs");
        for (String inputUuidString : inputs.keySet()) {
            UUID inputUuid = UUID.fromString(inputUuidString);
            if (inputNodes.containsKey(inputUuid)) continue; // already resolved recursively

            JsonObject inputObject = inputs.getAsJsonObject(inputUuidString);
            InputNode inputNode = loadInputNode(inputUuid, inputs, inputObject);
            inputNodes.put(inputUuid, inputNode);
        }

        // Operation nodes
        JsonObject operations = root.getAsJsonObject("operations");
        for (String operationUuidString : operations.keySet()) {
            UUID operationUuid = UUID.fromString(operationUuidString);

            OperationNode operationNode = operationNodes.get(operationUuid);
            if (operationNode == null) {
                JsonObject operationObject = operations.getAsJsonObject(operationUuidString);
                operationNode = loadOperationNode(operationUuid, operationObject);
                operationNodes.put(operationUuid, operationNode);
            }

            // TODO: event subscription stuff
        }
    }

    public OperationNode getEntrypoint() {
        return getOperationNode(this.entrypoint);
    }

    public OperationNode getOperationNode(UUID reference) {
        return operationNodes.get(reference);
    }
}
