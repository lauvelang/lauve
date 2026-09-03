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

public class ConditionGroup extends BlockGroup {
    public ConditionGroup() {
        super("condition", Palette.MOCHA.green().components());
    }

    private void defineComparison(Id id, InputPart.Controller controller) {
        define(Definition.builder(id)
                .shape(NodeShape.INPUT)
                .input("first", controller, "1")
                .label("operand")
                .input("second", controller, "2")
                .build());
    }


    @Override
    public void init() {
        defineComparison(id("equals"), InputPart.Controller.ANY);
        defineComparison(id("less_than"), InputPart.Controller.NUMBER);
        defineComparison(id("greater_than"), InputPart.Controller.NUMBER);

        define(Definition.builder(id("not"))
                .shape(NodeShape.INPUT)
                .label("not")
                .input("value", InputPart.Controller.ANY)
                .build());
    }
}
