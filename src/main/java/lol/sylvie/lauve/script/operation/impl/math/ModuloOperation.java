package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class ModuloOperation extends Operation {
    public ModuloOperation() {
        super("modulo");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        double first = number(context, args, "first");
        double second = number(context, args, "second");
        if (second == 0) return Double.MAX_VALUE;

        return first % second;
    }
}
