package lol.sylvie.lauve.std;

import lol.sylvie.lauve.Lauvelang;
import lol.sylvie.lauve.error.RuntimeError;
import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.events.api.Listener;
import lol.sylvie.lauve.interpreter.Interpreter;
import lol.sylvie.lauve.parsing.LauveCallable;
import lol.sylvie.lauve.parsing.Token;

import java.util.List;

public class LauveListener<T extends Event> extends Listener<T> {
    private final Interpreter interpreter;
    private final Token token;
    public LauveCallable callback;

    public LauveListener(Class<T> eventClass, Interpreter interpreter, Token token, LauveCallable callback) {
        super(eventClass);
        this.interpreter = interpreter;
        this.token = token;
        this.callback = callback;
    }


    @Override
    public void call(T t) {
        // TODO: implement ts
        try {
            callback.call(this.interpreter, List.of(), this.token);
        } catch (RuntimeError error) {
            Lauvelang.runtimeError(error);
        }
    }
}
