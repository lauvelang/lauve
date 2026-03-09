package lol.sylvie.lauve.parsing;

import lol.sylvie.lauve.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class LauveClass implements LauveCallable {
    public final String name;
    private final Map<String, LauveFunction> methods;

    public LauveClass(String name, Map<String, LauveFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    public LauveFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments,
                       Token token) {
        LauveInstance instance = new LauveInstance(this);
        LauveFunction initializer = findMethod(LauveFunction.INIT_NAME);
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments, token);
        }

        return instance;
    }

    @Override
    public int arity() {
        LauveFunction initializer = findMethod(LauveFunction.INIT_NAME);
        if (initializer == null) return 0;
        return initializer.arity();
    }
}
