package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.LauveClass;
import lol.sylvie.lauve.parsing.LauveFunction;

import java.util.Map;

public abstract class NativeLauveClass<T> extends LauveClass {
    Class<T> clazz;

    public NativeLauveClass(String name, Map<String, LauveFunction> methods, Class<T> clazz) {
        super(name, methods);
        this.clazz = clazz;
    }
}
