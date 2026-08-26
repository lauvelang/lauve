package lol.sylvie.lauve.script.operation;

//import lol.sylvie.lauve.script.operation.impl.control.SetupOperation;
import lol.sylvie.lauve.script.operation.impl.debug.DummyOperation;
import lol.sylvie.lauve.script.operation.impl.debug.LogOperation;
import lol.sylvie.lauve.script.operation.impl.variable.SetOperation;
import lol.sylvie.lauve.util.Id;
import lombok.Getter;

import java.util.HashMap;

public class Operations {
    private static final HashMap<Id, Operation> operations = new HashMap<>();

    private static void register(Operation operation) {
        System.out.println("Registering " + operation.getId());
        operations.put(operation.getId(), operation);
    }

    public static Operation get(Id key) {
        System.out.println("Querying " + key);
        return operations.get(key);
    }

    static {
        // Debug
        register(new DummyOperation());
        register(new LogOperation());

        // Variables
        register(new SetOperation());

        // Control
        //register(new SetupOperation());
    }
}
