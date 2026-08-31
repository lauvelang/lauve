package lol.sylvie.lauve.script.datagen.impl;

import com.catppuccin.Palette;
import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import net.minecraft.ChatFormatting;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.DyeColor;

import java.awt.*;

public class DebugGroup extends BlockGroup {
    public DebugGroup() {
        super("debug", Palette.MOCHA.pink().components());
    }

    @Override
    public void init() {
        define(Definition.builder(id("log"))
                .label("log")
                .input("text", InputPart.Controller.STRING)
                .label("destination")
                .build());
    }
}
