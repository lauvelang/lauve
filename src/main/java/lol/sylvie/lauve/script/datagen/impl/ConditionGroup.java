package lol.sylvie.lauve.script.datagen.impl;

import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;

public class ConditionGroup extends BlockGroup {
    public ConditionGroup() {
        super("condition");
    }

    @Override
    public void init() {
        define(Definition.builder(id("equals"))
                .shape(NodeShape.INPUT)
                .input("first", InputPart.Controller.ANY)
                .label("equals")
                .input("second", InputPart.Controller.ANY)
                .build());

        define(Definition.builder(id("not"))
                .shape(NodeShape.INPUT)
                .label("not")
                .input("value", InputPart.Controller.ANY)
                .build());
    }
}
