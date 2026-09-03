package lol.sylvie.lauve.script.operation.impl.condition;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class EqualsOperation extends Operation {
    public EqualsOperation() {
        super("equals");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object first = object(context, args, "first");
        Object second = object(context, args, "second");

        // If either are null, they can only be equal if both are null
        if (first == null || second == null) {
            return (first == null && second == null);
        }



        // Equate string values otherwise
        // TODO: Check if this catches all cases
        String firstValue = TypeCoercion.toString(first);
        String secondValue = TypeCoercion.toString(second);
        return firstValue.equals(secondValue);
    }
}
