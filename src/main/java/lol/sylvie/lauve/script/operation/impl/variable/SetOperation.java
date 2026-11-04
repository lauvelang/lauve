package lol.sylvie.lauve.script.operation.impl.variable;

import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.script.datagen.impl.EventGroup;
import lol.sylvie.lauve.script.operation.Operation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.script.Argument;
import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.util.Reflection;
import lol.sylvie.lauve.util.TypeCoercion;

import java.lang.reflect.Field;
import java.util.Map;

public class SetOperation extends Operation {
    private final boolean global;
    public SetOperation(String name, boolean global) {
        super(name);
        this.global = global;
    }

    @Override
    public Object operate(Context context, Node node, Map<String, Argument> args) {
        Object value = object(context, args, "value");

        Argument argument = args.get("key");
        if (!argument.isComputed() && !global) {
            Node subnode = context.getScript().getNode(argument.references());
            if (subnode.operation().getId().equals(EventGroup.ACCESSOR)) {
                Event event = context.getEvent();
                String name = string(context, subnode.args(), "variable");
                try {
                    Field field = event.getClass().getDeclaredField(name);
                    if (Reflection.isFinal(field)) return null;

                    field.setAccessible(true);
                    TypeCoercion.setField(event, field, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    context.warn("Could not set field {} ({})", name, e);
                }
                return null;
            }
        }

        String name = string(context, args, "key");

        context.setVariable(name, value, global);
        return null;
    }
}
