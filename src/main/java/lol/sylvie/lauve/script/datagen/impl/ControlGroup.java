package lol.sylvie.lauve.script.datagen.impl;

import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.util.Id;

// funny name
public class ControlGroup extends BlockGroup {
    public ControlGroup() {
        super("control");
    }

    protected void defineConditional(Id id) {
        define(Definition.builder(id)
                .hasChildren(true)
                .label("control")
                .input("conditional", InputPart.Controller.BOOLEAN)
                .build());
    }

    @Override
    public void init() {
        defineConditional(id("if"));
        defineConditional(id("while"));

        define(Definition.builder(id("load"))
                .shape(NodeShape.START)
                .label("label")
                .build());
    }
}
