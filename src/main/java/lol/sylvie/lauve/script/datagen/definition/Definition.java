package lol.sylvie.lauve.script.datagen.definition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.datagen.JsonSerializable;
import lol.sylvie.lauve.script.datagen.definition.part.InputPart;
import lol.sylvie.lauve.script.datagen.definition.part.LabelPart;
import lol.sylvie.lauve.script.datagen.definition.part.OptionPart;
import lol.sylvie.lauve.script.datagen.definition.part.Part;
import lol.sylvie.lauve.util.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Locale;

@Builder(builderMethodName = "")
public class Definition implements JsonSerializable {
    @Getter
    private final Id describes;

    @Builder.Default
    private NodeShape shape = NodeShape.NORMAL;

    @Singular("part")
    private List<Part> parts;

    @Builder.Default
    private boolean hasChildren = false;

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();

        root.addProperty("has_children", this.hasChildren);
        root.addProperty("shape", this.shape.name().toLowerCase(Locale.ROOT));

        JsonArray array = new JsonArray();
        this.parts.forEach(part -> array.add(part.toJson()));
        root.add("description", array);

        return root;
    }

    public static DefinitionBuilder builder(Id id) {
        return new DefinitionBuilder().describes(id);
    }

    public static class DefinitionBuilder {
        public DefinitionBuilder label(String id) {
            return this.part(new LabelPart(id));
        }

        public DefinitionBuilder input(String id, InputPart.Controller controller, @Nullable String sample) {
            return this.part(new InputPart(id, controller, sample));
        }

        public DefinitionBuilder input(String id, InputPart.Controller controller) {
            return this.input(id, controller, null);
        }


        public DefinitionBuilder option(String id, List<String> options) {
            return this.part(new OptionPart(id, options));
        }
    }
}