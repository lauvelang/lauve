package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class RoundOperation extends Operation {
    public RoundOperation() {
        super("round");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        double value = number(context, args, "value");

        return Math.round(value);
    }
}
