package lol.sylvie.lauve.script.operation.impl.debug;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class DummyOperation extends Operation {
    public DummyOperation() {
        super("dummy");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        return null;
    }
}
