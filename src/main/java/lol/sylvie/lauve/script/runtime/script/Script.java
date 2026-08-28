package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.util.Constants;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
public class Script {
    private final File file;

    private String entrypoint;
    private final HashMap<String, Node> operationNodes = new HashMap<>();

    public void load() {
        JsonObject root;
        try (FileReader reader = new FileReader(this.file)) {
            root = Constants.GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.entrypoint = root.get("entrypoint").getAsString();

        // Operation nodes
        JsonObject operations = root.getAsJsonObject("operations");
        for (String operationRid : operations.keySet()) {
            Node operationNode = operationNodes.get(operationRid);
            if (operationNode == null) {
                JsonObject operationObject = operations.getAsJsonObject(operationRid);
                operationNode = Node.load(operationRid, operationObject);
                operationNodes.put(operationRid, operationNode);
            }
        }
    }

    public Node getEntrypoint() {
        return getNode(this.entrypoint);
    }

    public Node getNode(String reference) {
        return operationNodes.get(reference);
    }
}
