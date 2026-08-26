package lol.sylvie.lauve.script.input;

import lol.sylvie.lauve.script.input.impl.variable.VariableInput;
import lol.sylvie.lauve.util.Id;

import java.util.HashMap;

public class Inputs {
    private static final HashMap<Id, Input> inputs = new HashMap<>();

    private static void register(Input input) {
        inputs.put(input.getId(), input);
    }

    public static Input get(Id key) {
        return inputs.get(key);
    }

    static {
        // Variables
        register(new VariableInput());
    }
}
