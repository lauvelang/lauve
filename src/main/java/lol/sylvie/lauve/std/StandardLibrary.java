package lol.sylvie.lauve.std;

import lol.sylvie.lauve.error.RuntimeError;
import lol.sylvie.lauve.events.LauveEvents;
import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.events.api.EventBus;
import lol.sylvie.lauve.events.api.Listener;
import lol.sylvie.lauve.interpreter.Environment;
import lol.sylvie.lauve.interpreter.Interpreter;
import lol.sylvie.lauve.parsing.LauveCallable;
import lol.sylvie.lauve.parsing.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StandardLibrary {
    public static void defineEventFunctions(Interpreter interpreter) {
        Environment globals = interpreter.globals;

        globals.define("on", new LauveCallable() {
            @Override
            public int arity() {
                return 2;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token token) {
                String value = (String) arguments.getFirst();
                Class<? extends Event> eventClass = LauveEvents.EVENTS.get(value);
                LauveCallable callback = (LauveCallable) arguments.get(1);

                if (eventClass == null) {
                    throw new RuntimeError(token, "Event not found: " + value);
                }

                Listener<?> listener = new LauveListener<>(eventClass, interpreter, token, callback);
                if (!interpreter.listeners.containsKey(eventClass)) {
                    interpreter.listeners.put(eventClass, new ArrayList<>());
                }

                interpreter.listeners.get(eventClass).add(listener);
                interpreter.callableCache.put(callback, listener);
                EventBus.subscribe(listener);

                return null;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });

        globals.define("off", new LauveCallable() {
            @Override
            public int arity() {
                return 1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments, Token token) {
                LauveCallable callback = (LauveCallable) arguments.getFirst();
                Listener<?> listener = interpreter.callableCache.get(callback);

                if (listener == null) {
                    return null;
                }

                interpreter.listeners.get(listener.getEventClass()).remove(listener);
                interpreter.callableCache.remove(callback);
                EventBus.unsubscribe(listener);

                return null;
            }
        });
    }

    public static void load(Interpreter interpreter) {
        Environment globals = interpreter.globals;
        globals.define("clock", new LauveCallable() {
            @Override
            public int arity() { return 0; }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments,
                               Token token) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });

       defineEventFunctions(interpreter);
    }
}
