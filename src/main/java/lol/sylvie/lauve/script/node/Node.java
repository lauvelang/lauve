package lol.sylvie.lauve.script.node;

import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.util.Id;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class Node {
    private final Operation operation;

    // These need to be non-final because nodes are created sequentially, and we'll need to backtrack a bit.
    @Setter
    private @Nullable Node parent;

    @Setter
    private @Nullable Node next;

    private final Map<String, Input> inputs;

    public Node(Operation operation, @Nullable Node parent, @Nullable Node next, Map<String, Input> inputs) {
        this.operation = operation;
        this.parent = parent;
        this.next = next;
        this.inputs = inputs;
    }
}
