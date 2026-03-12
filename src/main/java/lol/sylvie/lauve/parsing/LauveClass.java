package lol.sylvie.lauve.parsing;

import lol.sylvie.lauve.interpreter.Interpreter;

import java.util.List;
import java.util.Map;

public class LauveClass implements LauveCallable {
    public final String name;
    final LauveClass superclass;
    private final Map<String, LauveFunction> methods;

    public LauveClass(String name, LauveClass superclass,
                      Map<String, LauveFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    public LauveFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        if (superclass != null) {
            return superclass.findMethod(name);
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
