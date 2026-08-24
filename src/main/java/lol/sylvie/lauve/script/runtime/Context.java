package lol.sylvie.lauve.script.runtime;

import java.util.HashMap;

public class Context {
    private final Script script;
    private final HashMap<String, Object> locals = new HashMap<>();

    public Context(Script script) {
        this.script = script;
    }

    public Object getLocal(String name) {
        return locals.get(name);
    }

    public void setLocal(String name, Object value) {
        locals.put(name, value);
    }
}
