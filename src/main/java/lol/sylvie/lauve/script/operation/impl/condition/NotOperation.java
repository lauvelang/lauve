package lol.sylvie.lauve.script.operation.impl.condition;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class NotOperation extends Operation {
    public NotOperation() {
        super("not");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        return !bool(context, args, "value");
    }
}
