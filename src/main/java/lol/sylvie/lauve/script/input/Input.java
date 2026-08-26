package lol.sylvie.lauve.script.input;

import lol.sylvie.lauve.script.runtime.Context;
import lol.sylvie.lauve.script.runtime.node.InputNode;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.IdentifiedObject;

import java.util.Map;

public abstract class Input extends IdentifiedObject {
    public Input(Id id) {
        super(id);
    }

    public Input(String name) {
        super(name);
    }

    public abstract Object get(Context context, Map<String, Object> args);
}
