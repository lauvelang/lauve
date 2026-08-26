package lol.sylvie.lauve.script.input.impl.engine;

import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.script.runtime.Context;

import java.util.Map;

public class StaticInput extends Input {
    private final Object value;

    public StaticInput(Object value) {
        super("static");
        this.value = value;
    }

    @Override
    public Object get(Context context, Map<String, Object> args) {
        return this.value;
    }
}
