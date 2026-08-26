package lol.sylvie.lauve.script.operation;

//import lol.sylvie.lauve.script.operation.impl.control.SetupOperation;
import lol.sylvie.lauve.script.operation.impl.debug.DummyOperation;
import lol.sylvie.lauve.script.operation.impl.debug.LogOperation;
import lol.sylvie.lauve.script.operation.impl.math.*;
import lol.sylvie.lauve.script.operation.impl.variable.GetOperation;
import lol.sylvie.lauve.script.operation.impl.variable.SetOperation;
import lol.sylvie.lauve.util.Id;
import lombok.Getter;

import java.util.HashMap;
import java.util.function.Supplier;

public class Operations {
    private static final HashMap<Id, Operation> operations = new HashMap<>();

    private static void register(Operation operation) {
        operations.put(operation.getId(), operation);
    }

    private static void register(Supplier<Operation> operationSupplier) {
        register(operationSupplier.get());
    }

    public static Operation get(Id key) {
        return operations.get(key);
    }

    static {
        // Debug
        register(DummyOperation::new);
        register(LogOperation::new);

        // Math
        register(AddOperation::new);
        register(SubtractOperation::new);
        register(MultiplyOperation::new);
        register(DivideOperation::new);

        register(RandomNumberOperation::new);

        // Variables
        register(SetOperation::new);
        register(GetOperation::new);

        // Control
        //register(new SetupOperation());
    }
}
