package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
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
    public Object operate(Context context, Node node, Map<String, Object> args) {
        double origin = Types.asNumber(args.get("origin"));
        double bound = Types.asNumber(args.get("bound"));

        return random.nextDouble(origin, bound);
    }
}
