package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class MultiplyOperation extends Operation {
    public MultiplyOperation() {
        super("multiply");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        Object first = args.get("first");
        Object second = args.get("second");

         if (first instanceof Number firstNumber && second instanceof Number secondNumber) {
            return firstNumber.doubleValue() * secondNumber.doubleValue();
        }

         if (first instanceof String firstString && second instanceof Number repeat) {
             return firstString.repeat(repeat.intValue());
         } else if (second instanceof String secondString && first instanceof Number repeat) {
             return secondString.repeat(repeat.intValue());
         }

         return 0d;
    }
}
