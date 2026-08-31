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

    @Override
    public void init() {
        define(Definition.builder(id("set"))
                .label("set")
                .input("key", InputPart.Controller.VARIABLE)
                .label("to")
                .input("value", InputPart.Controller.ANY)
                .option("scope", List.of("local", "global"))
                .build());

        define(Definition.builder(id("get"))
                .shape(NodeShape.INPUT)
                .label("get")
                .input("key", InputPart.Controller.VARIABLE)
                .build());
    }
}
