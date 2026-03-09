package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.AbstractLauveInstance;
import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.parsing.Token;

public class NativeLauveInstance<T> extends AbstractLauveInstance {
    private T backing;

    public NativeLauveInstance(NativeLauveClass<T> klass, T backing) {
        super(klass);
        this.backing = backing;
    }

    @Override
    public Object get(Token name) {


        LauveFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        return null;
    }

    @Override
    public void set(Token name, Object value) {

    }
}
