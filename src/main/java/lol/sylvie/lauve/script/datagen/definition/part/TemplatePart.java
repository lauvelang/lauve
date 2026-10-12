package lol.sylvie.lauve.script.datagen.definition.part;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.util.Id;
import lol.sylvie.lauve.util.Serialization;

import java.util.Map;

public class TemplatePart extends Part {
    private final Id opcode;
    private final Map<String, Object> args;

    public TemplatePart(String id, Id opcode, Map<String, Object> args) {
        super("template", id);
        this.opcode = opcode;
        this.args = args;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        root.addProperty("opcode", this.opcode.toString());

        JsonObject args = new JsonObject();
        this.args.forEach((k, v) -> args.add(k, Serialization.closestJson(v)));
        root.add("args", args);

        return root;
    }
}
