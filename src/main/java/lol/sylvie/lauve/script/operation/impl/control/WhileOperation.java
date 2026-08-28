package lol.sylvie.lauve.script.operation.impl.control;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.interpreter.Interpreter;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class WhileOperation extends Operation {
    public WhileOperation() {
        super("while");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        while (bool(context, args, "condition")) {
            Node target = node(context, args, "child");
            Interpreter.walk(context, target);
        }

        return null;
    }
}
