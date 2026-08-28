package lol.sylvie.lauve.script.datagen;

import lol.sylvie.lauve.script.datagen.impl.*;

import java.util.List;

public class ScriptDatagen {
    static void main() {
        List<BlockGroup> blocks = List.of(
                new DebugGroup(),
                new VariableGroup(),
                new ConditionGroup(),
                new ControlGroup(),
                new MathGroup()
        );

        blocks.forEach(BlockGroup::write);
    }
}
