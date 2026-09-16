package lol.sylvie.lauve.script.operation.impl.condition;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class AndOperation extends Operation {
    public AndOperation() {
        super("and");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        boolean first = bool(context, args, "first");
        boolean second = bool(context, args, "second");

        return first && second;
    }
}
