package lol.sylvie.lauve.script.operation.impl.event;

import lol.sylvie.lauve.script.node.Node;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.util.Id;

import java.util.Map;

public class EventOperation extends Operation {
    public EventOperation() {
        super("callback");
    }

    @Override
    public void operate(Context context, Node node, Map<String, Object> args) {

    }
}
