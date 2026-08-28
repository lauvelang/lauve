package lol.sylvie.lauve.script.operation;

import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.IdentifiedObject;
import lol.sylvie.lauve.util.Types;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class Operation extends IdentifiedObject {
    public Operation(Id id) {
        super(id);
    }

    public Operation(String name) {
        super(name);
    }

    protected static Object object(Context context, Argument argument) {
        if (argument.isComputed()) {
            return argument.computed();
        }

        Node inner = context.getScript().getNode(argument.references());
        Operation operation = inner.operation();
        return operation.operate(context, inner, inner.args());
    }

    protected static Object object(Context context, Map<String, Argument> args, String key) {
        return object(context, args.get(key));
    }

    protected static Node node(Context context, Map<String, Argument> args, String key) {
        String reference = string(context, args, key);
        return context.getScript().getNode(reference);
    }

    protected static String string(Context context, Map<String, Argument> args, String key) {
        return Types.asString(object(context, args, key));
    }

    protected static Double number(Context context, Map<String, Argument> args, String key) {
        return Types.asNumber(object(context, args, key));
    }

    protected static Boolean bool(Context context, Map<String, Argument> args, String key) {
        return Types.asBool(object(context, args, key));
    }

    public abstract Object operate(Context context, Node node, Map<String, Argument> args);
}
