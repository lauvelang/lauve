package lol.sylvie.lauve.parsing;

import lol.sylvie.lauve.interpreter.Interpreter;

import java.util.List;

public interface LauveCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments, Token token);
}