package lol.sylvie.lauve.script.operation;

import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.OperationNode;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.IdentifiedObject;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class Operation extends IdentifiedObject {
    public Operation(Id id) {
        super(id);
    }

    public Operation(String name) {
        super(name);
    }

    public abstract void operate(Context context, OperationNode node, Map<String, Object> args);
}
