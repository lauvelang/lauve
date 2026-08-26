package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class SubtractOperation extends Operation {
    public SubtractOperation() {
        super("subtract");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        double first = Types.asNumber(args.get("first"));
        double second = Types.asNumber(args.get("second"));

        return first - second;
    }
}
