package lol.sylvie.lauve.script.operation;

//import lol.sylvie.lauve.script.operation.impl.control.SetupOperation;
import lol.sylvie.lauve.script.operation.impl.debug.LogOperation;
import lol.sylvie.lauve.script.operation.impl.variable.SetOperation;
import lol.sylvie.lauve.util.Id;
import lombok.Getter;

import java.util.HashMap;

public class Operations {
    private static HashMap<Id, Operation> operations;

    private static void register(Operation operation) {
        operations.put(operation.getId(), operation);
    }

    public static Operation get(Id key) {
        return operations.get(key);
    }

    static {
        // Debug
        register(new LogOperation());

        // Variables
        register(new SetOperation());

        // Control
        //register(new SetupOperation());
    }
}
