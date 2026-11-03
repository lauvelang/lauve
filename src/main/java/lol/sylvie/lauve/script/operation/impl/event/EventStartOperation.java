package lol.sylvie.lauve.script.operation.impl.event;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Id;

import java.util.Map;

public class EventStartOperation extends Operation {
    public EventStartOperation(String name) {
        super(name);
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        return null;
    }
}
