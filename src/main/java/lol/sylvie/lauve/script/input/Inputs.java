package lol.sylvie.lauve.script.input;

import lol.sylvie.lauve.script.input.impl.variable.VariableInput;
import lol.sylvie.lauve.util.Id;

import java.util.HashMap;

public class Inputs {
    private static HashMap<Id, Input> inputs;

    private static void register(Input input) {
        inputs.put(input.getId(), input);
    }

    static {
        // Variables
        register(new VariableInput());
    }
}
