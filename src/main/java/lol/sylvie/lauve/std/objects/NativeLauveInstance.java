package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.AbstractLauveInstance;
import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.parsing.Token;

/*
 * While Lauve is similar to Python in that each instance can have its own unique fields,
 * Java has fields unique to each class.
 *
 * This class defers all instance gets/sets to the class.
 */
@SuppressWarnings("unchecked")
public class NativeLauveInstance<T> extends AbstractLauveInstance {
    private final T backing;

    public NativeLauveInstance(NativeLauveClass<T> klass, T backing) {
        super(klass);
        this.backing = backing;
    }

    @Override
    public Object get(Token name) {
        return ((NativeLauveClass<T>) this.klass).get(backing, name);
    }

    @Override
    public void set(Token name, Object value) {
        ((NativeLauveClass<T>) this.klass).set(backing, name, value);
    }
}
