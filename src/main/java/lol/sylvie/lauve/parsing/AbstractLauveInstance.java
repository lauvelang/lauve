package lol.sylvie.lauve.parsing;

public abstract class AbstractLauveInstance {
    protected LauveClass klass;

    public AbstractLauveInstance(LauveClass klass) {
        this.klass = klass;
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }

    public abstract Object get(Token name);

    public abstract void set(Token name, Object value);
}
