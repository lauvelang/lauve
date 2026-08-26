package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.util.Constants;
import lol.sylvie.lauve.util.Serialization;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class Script {
    private final File file;

    private UUID entrypoint;
    private final HashMap<UUID, Node> operationNodes = new HashMap<>();



    public void load() {
        JsonObject root;
        try (FileReader reader = new FileReader(this.file)) {
            root = Constants.GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.entrypoint = Serialization.uuidOf(root, "entrypoint");

        // Operation nodes
        JsonObject operations = root.getAsJsonObject("operations");
        for (String operationUuidString : operations.keySet()) {
            UUID operationUuid = UUID.fromString(operationUuidString);

            Node operationNode = operationNodes.get(operationUuid);
            if (operationNode == null) {
                JsonObject operationObject = operations.getAsJsonObject(operationUuidString);
                operationNode = Node.load(operationUuid, operationObject);
                operationNodes.put(operationUuid, operationNode);
            }
        }
    }

    public Node getEntrypoint() {
        return getNode(this.entrypoint);
    }

    public Node getNode(UUID reference) {
        return operationNodes.get(reference);
    }
}
