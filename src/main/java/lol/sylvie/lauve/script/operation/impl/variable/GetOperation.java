package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.script.runtime.Runtime;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Types;

import java.util.Map;

public class GetOperation extends Operation {
    public GetOperation() {
        super("get");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        String name = Types.asString(args.get("key"));
        Object local = context.getLocal(name);
        if (local != null) return local;

        return Runtime.getGlobal(name);
    }
}
