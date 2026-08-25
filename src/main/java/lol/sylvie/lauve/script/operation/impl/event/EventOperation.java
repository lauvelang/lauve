package lol.sylvie.lauve.script.operation.impl.event;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.OperationNode;

import java.util.Map;

public class EventOperation extends Operation {
    public EventOperation() {
        super("callback");
    }

    @Override
    public void operate(Context context, OperationNode node, Map<String, Object> args) {

    }
}
