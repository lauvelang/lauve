package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.datagen.impl.EventGroup;
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
        Object value = object(context, args, "value");

        Argument argument = args.get("key");
        if (!argument.isComputed()) {
            Node subnode = node(context, args, "key");
            if (subnode.operation().getId().equals(EventGroup.ACCESSOR)) {
                context.log("PRETEND WE SET EVENT VARIABLE");

                return null;
            }
        }

        String name = string(context, args, "key");

        context.setVariable(name, value, bool(context, args, "global"));
        return null;
    }
}
