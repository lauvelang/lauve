package lol.sylvie.lauve.script.runtime;

import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.node.InputNode;
import lol.sylvie.lauve.script.runtime.node.OperationNode;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {
    public static HashMap<String, Object> evaluate(Context context, HashMap<String, InputNode> args) {
        HashMap<String, Object> out = new HashMap<>();

        args.forEach((key, node) -> {
            Input input = node.input();
            out.put(key, input.get(context, node.args()));
        });

        return out;
    }

    public static void walk(Context context, OperationNode start) {
        OperationNode current = start;
        do {
            Operation operation = current.operation();
            HashMap<String, Object> args = evaluate(context, current.args());
            operation.operate(context, current, args);

            current = context.getScript().getOperationNode(current.next());
            context.setWorking(current);
        } while (current != null);
    }
}
