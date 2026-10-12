package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class GetOperation extends Operation {
    public GetOperation() {
        super("get");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        String name = string(context, args, "key");
        return context.getVariable(name);
    }
}
