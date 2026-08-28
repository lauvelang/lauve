package lol.sylvie.lauve.script.operation.impl.debug;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class LogOperation extends Operation {
    public LogOperation() {
        super("log");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        System.out.println(string(context, args, "text"));
        return null;
    }
}
