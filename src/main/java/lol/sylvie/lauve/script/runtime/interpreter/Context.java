package lol.sylvie.lauve.script.runtime.interpreter;

import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.script.runtime.script.Script;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

// Interpreter context
@RequiredArgsConstructor
public class Context {
    @Getter
    private final Script script;

    @Setter
    private Node working;
    private final HashMap<String, Object> locals = new HashMap<>();

    public Object getLocal(String name) {
        return locals.get(name);
    }

    public void setLocal(String name, Object value) {
        locals.put(name, value);
    }
}
