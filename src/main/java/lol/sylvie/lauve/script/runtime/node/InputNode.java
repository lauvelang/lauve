package lol.sylvie.lauve.script.runtime.node;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.input.Input;
import lol.sylvie.lauve.util.Id;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public record InputNode(
        UUID rid,
        Input input,
        @Nullable HashMap<String, InputNode> args) {
}
