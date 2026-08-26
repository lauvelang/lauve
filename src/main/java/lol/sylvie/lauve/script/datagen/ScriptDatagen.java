package lol.sylvie.lauve.script.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lol.sylvie.lauve.script.datagen.impl.DebugGroup;
import lol.sylvie.lauve.script.datagen.impl.EventGroup;
import lol.sylvie.lauve.script.datagen.impl.VariableGroup;

import java.util.List;

public class ScriptDatagen {
    static void main() {
        List<BlockGroup> blocks = List.of(
                new DebugGroup(),
                new EventGroup(),
                new VariableGroup()
        );

        blocks.forEach(BlockGroup::write);
    }
}
