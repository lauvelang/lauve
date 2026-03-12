package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.LauveClass;
import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.parsing.Token;

import java.util.Map;

public abstract class NativeLauveClass<T> extends LauveClass {
    Class<T> clazz;

    public NativeLauveClass(String name, LauveClass superclass, Map<String, LauveFunction> methods, Class<T> clazz) {
        super(name, superclass, methods);
        this.clazz = clazz;
    }

    public Object get(T object, Token name) {

        return null;
    }

    public void set(T object, Token name, Object value) {

    }
}
