package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.manager.ScriptManager;
import lol.sylvie.lauve.script.node.Node;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class SetOperation extends Operation {
    public SetOperation() {
        super("set");
    }

    @Override
    public void operate(Context context, Node node, Map<String, Object> args) {
        String name = Types.string(args.get("key"));
        Object value = args.get("value");

        if (Types.bool(args.get("global"))) {
            ScriptManager.setGlobal(name, value);
        } else {
            context.setLocal(name, value);
        }
    }
}
