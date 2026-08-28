package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Types;

import java.util.Map;
import java.util.Random;

public class RandomNumberOperation extends Operation {
    private final Random random = new Random();

    public RandomNumberOperation() {
        super("random_number");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        double origin = number(context, args, "origin");
        double bound = number(context, args, "bound");

        return random.nextDouble(origin, bound);
    }
}
