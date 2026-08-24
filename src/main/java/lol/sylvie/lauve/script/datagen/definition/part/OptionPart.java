package lol.sylvie.lauve.script.datagen.definition.part;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class OptionPart extends Part {
    private final List<String> options;

    public OptionPart(String id, List<String> options) {
        super("option", id);
        this.options = options;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();

        JsonArray array = new JsonArray();
        options.forEach(array::add);
        root.add("options", array);

        return root;
    }
}
