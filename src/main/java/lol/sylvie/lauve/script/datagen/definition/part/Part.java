package lol.sylvie.lauve.script.datagen.definition.part;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.datagen.JsonSerializable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Part implements JsonSerializable {
    private final String type;
    private final String id;

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.addProperty("type", this.type);
        root.addProperty("id", this.id);

        return root;
    }
}
