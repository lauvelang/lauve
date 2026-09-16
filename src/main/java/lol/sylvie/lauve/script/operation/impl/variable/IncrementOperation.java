package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Runtime;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class IncrementOperation extends Operation {
    public IncrementOperation() {
        super("increment");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        String name = string(context, args, "key");
        double amount = number(context, args, "amount");

        Object reference = context.getVariable(name);
        double current = TypeCoercion.toNumber(reference);

        context.setVariable(name, current + amount, bool(context, args, "global"));
        return null;
    }
}