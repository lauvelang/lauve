package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class AddOperation extends Operation {
    public AddOperation() {
        super("add");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object first = object(context, args, "first");
        Object second = object(context, args, "second");

        boolean isFirstNumber = TypeCoercion.canBeNumber(first);
        boolean isSecondNumber = TypeCoercion.canBeNumber(second);

         if (isFirstNumber && isSecondNumber) {
            return TypeCoercion.toNumber(first) + TypeCoercion.toNumber(second);
        }

         return TypeCoercion.toString(first) + TypeCoercion.toString(second);
    }
}
