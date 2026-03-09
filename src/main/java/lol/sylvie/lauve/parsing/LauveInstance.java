package lol.sylvie.lauve.parsing;

import lol.sylvie.lauve.error.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class LauveInstance extends AbstractLauveInstance {
    private final Map<String, Object> fields = new HashMap<>();

    public LauveInstance(LauveClass klass) {
        super(klass);
    }


    public Object get(Token name) {
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        LauveFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name,
                "Undefined property '" + name.lexeme + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }
}
