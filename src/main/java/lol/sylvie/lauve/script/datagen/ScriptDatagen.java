package lol.sylvie.lauve.script.datagen;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.datagen.impl.*;
import lol.sylvie.lauve.util.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        JsonObject root = new JsonObject();
        blocks.forEach((group) -> group.write(root));

        try (FileWriter writer = new FileWriter("./blocks.json")) {
            Constants.GSON.toJson(root, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
