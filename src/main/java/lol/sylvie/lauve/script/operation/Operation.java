package lol.sylvie.lauve.script.operation;

import lol.sylvie.lauve.script.node.Node;
import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.Reflection;
import lombok.Getter;

import java.util.Map;

@Getter
public abstract class Operation {
    private final Id id;

    protected Operation(Id id) {
        this.id = id;
    }

    protected Operation(String name) {
        this.id = new Id(Reflection.getParentPackage(this.getClass()), name);
    }

    public abstract void operate(Context context, Node node, Map<String, Object> args);
}
