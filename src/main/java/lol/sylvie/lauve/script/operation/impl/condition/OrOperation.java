package lol.sylvie.lauve.script.operation.impl.condition;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class OrOperation extends Operation {
    public OrOperation() {
        super("or");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        boolean first = bool(context, args, "first");
        boolean second = bool(context, args, "second");

        return first || second;
    }
}
