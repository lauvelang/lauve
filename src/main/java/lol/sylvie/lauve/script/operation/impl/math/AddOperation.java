package lol.sylvie.lauve.script.operation.impl.math;

import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.util.Map;

public class AddOperation extends Operation {
    public AddOperation() {
        super("add");
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object first = object(context, args, "first");
        Object second = object(context, args, "second");

         if (first instanceof Number firstNumber && second instanceof Number secondNumber) {
            return firstNumber.doubleValue() + secondNumber.doubleValue();
        } else if (first instanceof Boolean firstBool && second instanceof Boolean secondBool) {
            return firstBool && secondBool;
        }

         return String.valueOf(first) + second;
    }
}
