package lol.sylvie.lauve.script.datagen.definition.part;

import com.google.gson.JsonObject;
import lombok.Getter;

public class LabelPart extends Part {
    @Getter
    private final int stack;

    public LabelPart(String id, int stack) {
        super("label", id);
        this.stack = stack;
    }

    public LabelPart(String id) {
        this(id, -1);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();

        if (stack != -1) root.addProperty("stack", stack);

        return root;
    }
}
