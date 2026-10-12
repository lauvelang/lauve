package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class SetOperation extends Operation {
    public SetOperation() {
        super("set");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        String name = string(context, args, "key");
        Object value = object(context, args, "value");

        context.setVariable(name, value, bool(context, args, "global"));
        return null;
    }
}
