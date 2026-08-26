package lol.sylvie.lauve.script.operation.impl.debug;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.OperationNode;

import java.util.Map;

public class LogOperation extends Operation {
    public LogOperation() {
        super("log");
    }

    @Override
    public void operate(Context context, OperationNode node, Map<String, Object> args) {
        System.out.println("TEXT: " + args.get("text"));
    }
}
