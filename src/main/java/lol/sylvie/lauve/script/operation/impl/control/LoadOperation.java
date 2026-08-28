package lol.sylvie.lauve.script.operation.impl.control;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class LoadOperation extends Operation {
    public LoadOperation() {
        super("load");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        return null;
    }
}
