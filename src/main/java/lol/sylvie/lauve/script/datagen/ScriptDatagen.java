package lol.sylvie.lauve.script.datagen;

import lol.sylvie.lauve.script.datagen.impl.DebugGroup;
import lol.sylvie.lauve.script.datagen.impl.MathGroup;
import lol.sylvie.lauve.script.datagen.impl.VariableGroup;

import java.util.List;

public class ScriptDatagen {
    static void main() {
        List<BlockGroup> blocks = List.of(
                new DebugGroup(),
                new VariableGroup(),
                new MathGroup()
        );

        blocks.forEach(BlockGroup::write);
    }
}
