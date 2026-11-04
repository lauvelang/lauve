package lol.sylvie.lauve.script.datagen.impl;

import com.catppuccin.Palette;
import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.DyeColor;

import java.awt.*;
import java.util.List;

public class VariableGroup extends BlockGroup {
    public VariableGroup() {
        super("variable", Palette.MOCHA.mauve().components());
    }

    private Definition.DefinitionBuilder setDefinition(String name) {
        return Definition.builder(id(name))
                .label("set")
                .input("key", InputPart.Controller.VARIABLE)
                .label("to")
                .input("value", InputPart.Controller.ANY, "0");
    }

    @Override
    public void init() {
        define(setDefinition("set")
                .build());
        define(setDefinition("set_global")
                .label("globally")
                .build());

        define(Definition.builder(id("get"))
                .shape(NodeShape.INPUT)
                .label("get")
                .input("key", InputPart.Controller.VARIABLE)
                .build());

        define(Definition.builder(id("increment"))
                .label("increment")
                .input("key", InputPart.Controller.VARIABLE)
                .label("by")
                .input("amount", InputPart.Controller.NUMBER)
                .build());
    }
}
