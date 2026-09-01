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

public class MathGroup extends BlockGroup {
    public MathGroup() {
        super("math", Palette.MOCHA.red().components());
    }

    private void defineDualOperand(Id id) {
        define(Definition.builder(id)
                .shape(NodeShape.INPUT)
                .input("first", InputPart.Controller.NUMBER, "2")
                .label("operand")
                .input("second", InputPart.Controller.NUMBER, "2")
                .build());
    }

    @Override
    public void init() {
        defineDualOperand(id("add"));
        defineDualOperand(id("subtract"));
        defineDualOperand(id("multiply"));
        defineDualOperand(id("divide"));

        define(Definition.builder(id("random_number"))
                .shape(NodeShape.INPUT)
                .label("generate")
                .input("origin", InputPart.Controller.NUMBER, "1")
                .label("to")
                .input("bound", InputPart.Controller.NUMBER, "10")
                .build());
    }
}
