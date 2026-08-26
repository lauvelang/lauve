package lol.sylvie.lauve.script.datagen.impl;

import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;

public class DebugGroup extends BlockGroup {
    public DebugGroup() {
        super("debug");
    }

    @Override
    public void init() {
        define(Definition.builder(id("log"))
                .label("log")
                .input("text", InputPart.Controller.STRING)
                .build());
    }
}
