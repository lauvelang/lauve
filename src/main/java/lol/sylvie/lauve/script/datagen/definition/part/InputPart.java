package lol.sylvie.lauve.script.datagen.definition.part;

import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

public class InputPart extends Part {
    private final Controller controller;
    @Nullable
    private final String sample;

    public InputPart(String id, Controller controller, String sample) {
        super("input", id);
        this.controller = controller;
        this.sample = sample;
    }

    public enum Controller {
        ANY,
        VARIABLE,
        NUMBER,
        STRING,
        BOOLEAN,
        SELECT
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        root.addProperty("controller", this.controller.name().toLowerCase(Locale.ROOT));
        root.addProperty("sample", this.sample);

        return root;
    }
}
