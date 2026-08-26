package lol.sylvie.lauve.script.operation.impl.debug;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class LogOperation extends Operation {
    public LogOperation() {
        super("log");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        System.out.println(args.get("text"));
        return null;
    }
}
