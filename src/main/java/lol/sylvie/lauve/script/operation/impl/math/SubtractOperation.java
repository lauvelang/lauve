package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class SubtractOperation extends Operation {
    public SubtractOperation() {
        super("subtract");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        double first = number(context, args, "first");
        double second = number(context, args, "second");

        return first - second;
    }
}
