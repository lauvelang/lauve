package lol.sylvie.lauve.script.operation.impl.event;

import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.script.datagen.impl.EventGroup;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;

import java.lang.reflect.Field;
import java.util.Map;

public class EventAccessOperation extends Operation {
    public EventAccessOperation() {
        super(EventGroup.ACCESSOR);
    }

    // Getter
    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Event event = context.getEvent();
        String key = string(context, args, "variable");

        try {
            Field field = event.getClass().getDeclaredField(key);
            field.setAccessible(true);

            return field.get(event);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            context.warn("Couldn't access field {} for event {} ({})", key, event.getClass(), e);
        }

        return null;
    }
}
