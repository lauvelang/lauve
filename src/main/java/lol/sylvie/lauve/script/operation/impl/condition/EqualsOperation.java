package lol.sylvie.lauve.script.operation.impl.condition;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class EqualsOperation extends Operation {
    public EqualsOperation() {
        super("equals");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object first = object(context, args, "first");
        Object second = object(context, args, "second");

        // TODO: handle different types
        return first.equals(second);
    }
}
