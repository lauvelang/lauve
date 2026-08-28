package lol.sylvie.lauve.script.runtime.interpreter;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.script.runtime.script.Script;

import java.util.HashMap;

public class Interpreter {
    public static void walk(Context context, Node start) {
        Node current = start;
        do {
            Operation operation = current.operation();
            operation.operate(context, current, current.args());

            current = context.getScript().getNode(current.next());
            context.setWorking(current);
        } while (current != null);
    }
}
