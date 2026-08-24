package lol.sylvie.lauve.script.input;

import lol.sylvie.lauve.script.manager.ScriptManager;
import lol.sylvie.lauve.script.runtime.Context;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VariableInput implements Input {
    private final String name;

    @Override
    public Object get(Context context) {
        Object local = context.getLocal(name);
        if (local != null) return local;

        return ScriptManager.getGlobal(name);
    }
}
