package lol.sylvie.lauve.script.datagen.impl;

import lol.sylvie.lauve.script.datagen.BlockGroup;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.script.datagen.definition.NodeShape;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.util.Id;

public class MathGroup extends BlockGroup {
    public MathGroup() {
        super("math");
    }

    private void defineDualOperand(Id id) {
        define(Definition.builder(id)
                .shape(NodeShape.INPUT)
                .input("first", InputPart.Controller.NUMBER)
                .label("operand")
                .input("second", InputPart.Controller.NUMBER)
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
                .input("origin", InputPart.Controller.NUMBER)
                .label("to")
                .input("bound", InputPart.Controller.NUMBER)
                .build());
    }
}
