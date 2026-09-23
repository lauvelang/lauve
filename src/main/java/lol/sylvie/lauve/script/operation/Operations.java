package lol.sylvie.lauve.script.operation;

//import lol.sylvie.lauve.script.operation.impl.control.SetupOperation;
import lol.sylvie.lauve.script.operation.impl.condition.*;
import lol.sylvie.lauve.script.operation.impl.control.IfElseOperation;
import lol.sylvie.lauve.script.operation.impl.control.IfOperation;
import lol.sylvie.lauve.script.operation.impl.control.LoadOperation;
import lol.sylvie.lauve.script.operation.impl.control.WhileOperation;
import lol.sylvie.lauve.script.operation.impl.debug.LogOperation;
import lol.sylvie.lauve.script.operation.impl.math.*;
import lol.sylvie.lauve.script.operation.impl.variable.GetOperation;
import lol.sylvie.lauve.script.operation.impl.variable.IncrementOperation;
import lol.sylvie.lauve.script.operation.impl.variable.SetOperation;
import lol.sylvie.lauve.util.Id;

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
        register(LogOperation::new);

        // Math
        register(AddOperation::new);
        register(SubtractOperation::new);
        register(MultiplyOperation::new);
        register(DivideOperation::new);
        register(ModuloOperation::new);

        register(RandomNumberOperation::new);
        register(RoundOperation::new);

        // Conditions
        register(NotOperation::new);
        register(EqualsOperation::new);
        register(LessThanOperation::new);
        register(GreaterThanOperation::new);

        register(AndOperation::new);
        register(OrOperation::new);

        // Variables
        register(SetOperation::new);
        register(GetOperation::new);
        register(IncrementOperation::new);

        // Control
        register(IfOperation::new);
        register(IfElseOperation::new);
        register(WhileOperation::new);
        register(LoadOperation::new);
    }
}
