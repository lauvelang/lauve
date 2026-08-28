package lol.sylvie.lauve.script.operation.impl.control;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.interpreter.Interpreter;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class IfOperation extends Operation {
    public IfOperation() {
        super("if");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        if (Types.asBool(args.get("condition"))) {
            String rid = Types.asString(args.get("child"));
            Node target = context.getScript().getNode(rid);
            Interpreter.walk(context, target);
        }

        return null;
    }
}
