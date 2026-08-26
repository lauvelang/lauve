package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class AddOperation extends Operation {
    public AddOperation() {
        super("add");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Object> args) {
        Object first = args.get("first");
        Object second = args.get("second");

         if (first instanceof Number firstNumber && second instanceof Number secondNumber) {
            return firstNumber.doubleValue() + secondNumber.doubleValue();
        } else if (first instanceof Boolean firstBool && second instanceof Boolean secondBool) {
            return firstBool && secondBool;
        }

         return String.valueOf(first) + second;
    }
}
