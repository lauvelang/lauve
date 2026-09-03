package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.TypeCoercion;

import java.util.Map;

public class MultiplyOperation extends Operation {
    public MultiplyOperation() {
        super("multiply");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object first = object(context, args, "first");
        Object second = object(context, args, "second");

        boolean isFirstNumber = TypeCoercion.canBeNumber(first);
        boolean isSecondNumber = TypeCoercion.canBeNumber(second);

         if (isFirstNumber && isSecondNumber) {
             return TypeCoercion.toNumber(first) * TypeCoercion.toNumber(second);
        } else if (isFirstNumber || isSecondNumber) {
             int repeat = (int) TypeCoercion.toNumber(isFirstNumber ? first : second);
             return TypeCoercion.toString(isFirstNumber ? second : first).repeat(repeat);
         }

         context.warn("Attempted to multiply two non-number objects.");
         return 0d;
    }
}
