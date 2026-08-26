package lol.sylvie.lauve.script.input.impl.variable;

import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.script.manager.ScriptManager;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.InputNode;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class VariableInput extends Input {
    public VariableInput() {
        super("get");
    }

    @Override
    public Object get(Context context, Map<String, Object> args) {
        String name = Types.string(args.get("name"));
        Object local = context.getLocal(name);
        if (local != null) return local;

        return ScriptManager.getGlobal(name);
    }
}
