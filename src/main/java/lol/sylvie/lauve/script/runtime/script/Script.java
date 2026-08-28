package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.util.Constants;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class Script {
    private final File file;

    private final HashMap<String, Node> operationNodes = new HashMap<>();

    public void load() {
        JsonObject root;
        try (FileReader reader = new FileReader(this.file)) {
            root = Constants.GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public Node getNode(String reference) {
        return operationNodes.get(reference);
    }

    public Collection<Node> allNodes() {
        return operationNodes.values();
    }
}
