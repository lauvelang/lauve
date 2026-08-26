package lol.sylvie.lauve.script.operation.impl.debug;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.OperationNode;
import lol.sylvie.lauve.util.Id;

import java.util.Map;

public class DummyOperation extends Operation {
    public DummyOperation() {
        super("dummy");
    }

    @Override
    public void operate(Context context, OperationNode node, Map<String, Object> args) {

    }
}
