package lol.sylvie.lauve.script.datagen.impl;

import com.catppuccin.Palette;
import lol.sylvie.lauve.events.LauveEvents;
import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.util.Id;

import java.lang.reflect.Field;
import java.util.Map;

public class EventGroup extends BlockGroup {
    public static final String NAMESPACE = "event";
    public static final Id ACCESSOR = new Id(NAMESPACE, "accessor");

    public EventGroup() {
        super(NAMESPACE, Palette.MOCHA.yellow().components());
    }

    @Override
    public void init() {
        // Event variable accessor
        define(Definition.builder(ACCESSOR)
                .shape(NodeShape.INPUT)
                .label("event")
                .input("variable", InputPart.Controller.STRING, "variable")
                .build());

        // Specific event blocks
        LauveEvents.EVENTS.forEach((id, clazz) -> {
            Definition.DefinitionBuilder builder = Definition.builder(id(id))
                    .shape(NodeShape.START)
                    .label("⚡") // HACK
                    .label("name");

            for (Field field : clazz.getDeclaredFields()) {
                // TODO: add a "hidden" decorator so weirdo fields don't get exposed here
                String name = field.getName();
                builder.template("template_" + name, ACCESSOR, Map.of("variable", name));
            }

            define(builder.build());
        });
    }
}
