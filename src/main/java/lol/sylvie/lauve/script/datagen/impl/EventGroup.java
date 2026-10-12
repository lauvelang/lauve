package lol.sylvie.lauve.script.datagen.impl;

import com.catppuccin.Palette;
import lol.sylvie.lauve.events.LauveEvents;
import lol.sylvie.lauve.events.api.Event;
import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.util.Id;

import java.lang.reflect.Field;
import java.util.Map;

public class EventGroup extends BlockGroup {
    public EventGroup() {
        super("event", Palette.MOCHA.yellow().components());
    }

    @Override
    public void init() {
        // Event variable accessor
        Id accessorId = id("accessor");
        define(Definition.builder(accessorId)
                .shape(NodeShape.INPUT)
                .label("event")
                .input("variable", InputPart.Controller.STRING, "player")
                .build());

        LauveEvents.EVENTS.forEach((id, clazz) -> {
            Definition.DefinitionBuilder builder = Definition.builder(id(id))
                    .shape(NodeShape.START)
                    .label("name");

            for (Field field : clazz.getDeclaredFields()) {
                System.out.println(field);
                // TODO: add a "hidden" decorator so weirdo fields don't get exposed here
                String name = field.getName();
                builder.template("template_" + name, accessorId, Map.of("variable", name));
            }

            define(builder.build());
        });
    }
}
