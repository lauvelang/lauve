package lol.sylvie.lauve.script.datagen.impl;

import com.catppuccin.Palette;
import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.util.Id;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.DyeColor;

import java.awt.*;

// funny name
public class ControlGroup extends BlockGroup {
    public ControlGroup() {
        super("control", Palette.MOCHA.peach().components());
    }

    protected void defineConditional(Id id) {
        define(Definition.builder(id)
                .hasChildren(true)
                .label("control")
                .input("condition", InputPart.Controller.BOOLEAN, "true")
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
