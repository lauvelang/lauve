package lol.sylvie.lauve.script.operation.impl.control;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.interpreter.Interpreter;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class IfElseOperation extends Operation {
    public IfElseOperation() {
        super("ifelse");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Node target = substack(context, args, bool(context, args, "condition") ? 0 : 1);
        Interpreter.walk(context, target);

        return null;
    }
}
