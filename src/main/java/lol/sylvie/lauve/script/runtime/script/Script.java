package lol.sylvie.lauve.script.runtime.script;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.Lauve;
import lol.sylvie.lauve.events.LauveEvents;
import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.events.api.EventBus;
import lol.sylvie.lauve.events.api.Listener;
import lol.sylvie.lauve.script.datagen.impl.EventGroup;
import lol.sylvie.lauve.script.operation.impl.control.LoadOperation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.interpreter.Interpreter;
import lol.sylvie.lauve.util.Constants;
import lol.sylvie.lauve.util.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@RequiredArgsConstructor
public class Script {
    @Getter
    private final File file;

    private final HashMap<String, Node> operationNodes = new HashMap<>();

    private final HashSet<Listener<?>> eventListeners = new HashSet<>();

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

    public void unregisterListeners() {
        // todo: unsubscribe is inefficient with multiple items
        eventListeners.forEach(EventBus::unsubscribe);
    }

    public void registerListeners() {
        for (Node node : this.allNodes()) {
            Id opcode = node.operation().getId();
            if (!opcode.namespace().equals(EventGroup.NAMESPACE) || opcode.equals(EventGroup.ACCESSOR)) continue;

            String eventId = opcode.path();
            Class<? extends Event> eventClazz = LauveEvents.EVENTS.get(eventId);
            if (eventClazz == null) {
                Lauve.LOGGER.warn("Couldn't find event for block {}", eventId);
                continue;
            }

            Listener<?> listener = new Listener(eventClazz) {
                @Override
                public void call(Event event) {
                    Context context = new Context(Script.this, event);
                    Interpreter.walk(context, node);
                }
            };
            eventListeners.add(listener);
            EventBus.subscribe(listener);
        }
    }

    public void run() {
        for (Node node : this.allNodes()) {
            Context context = new Context(this);
            if (node.operation() instanceof LoadOperation) {
                Interpreter.walk(context, node);
            }
        }

        // I want any loading setup to be done before listeners are registered
        registerListeners();
    }

    public Node getNode(String reference) {
        return operationNodes.get(reference);
    }

    public Collection<Node> allNodes() {
        return operationNodes.values();
    }
}
