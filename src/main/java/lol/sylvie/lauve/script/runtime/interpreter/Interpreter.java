package lol.sylvie.lauve.script.runtime.interpreter;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.script.runtime.script.Script;

import java.util.HashMap;

public class Interpreter {
    public static HashMap<String, Object> evaluate(Context context, HashMap<String, Argument> args) {
        HashMap<String, Object> complete = new HashMap<>();
        args.forEach((key, node) -> {
            if (node.isComputed()) {
                complete.put(key, node.computed());
                return;
            }

            Node inner = context.getScript().getNode(node.references());
            Operation operation = inner.operation();
            HashMap<String, Object> innerArgs = evaluate(context, inner.args());
            Object resolved = operation.operate(context, inner, innerArgs);
            complete.put(key, resolved);
        });

        return complete;
    }

    public static void walk(Context context, Node start) {
        Node current = start;
        do {
            Operation operation = current.operation();
            HashMap<String, Object> args = evaluate(context, current.args());
            operation.operate(context, current, args);

            current = context.getScript().getNode(current.next());
            context.setWorking(current);
        } while (current != null);
    }
}
