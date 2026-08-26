package lol.sylvie.lauve.script.runtime;

import lol.sylvie.lauve.script.runtime.node.OperationNode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

// Interpreter context
@RequiredArgsConstructor
public class Context {
    private final Script script;

    @Setter
    private OperationNode working;
    private final HashMap<String, Object> locals = new HashMap<>();

    public Object getLocal(String name) {
        return locals.get(name);
    }

    public void setLocal(String name, Object value) {
        locals.put(name, value);
    }
}
