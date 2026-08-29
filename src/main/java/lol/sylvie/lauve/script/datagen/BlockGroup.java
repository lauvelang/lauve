package lol.sylvie.lauve.script.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.util.Constants;
import lol.sylvie.lauve.util.Id;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public abstract class BlockGroup {
    private final HashMap<Id, Definition> definitions = new HashMap<>();

    private final String namespace;
    protected final File folder;
    protected final Color color;

    protected BlockGroup(String namespace, Color color) {
        this.namespace = namespace;
        this.color = color;

        this.folder = new File("./" + this.namespace);
    }

    protected void define(Definition definition) {
        this.definitions.put(definition.getDescribes(), definition);
    }

    protected Id id(String path) {
        return new Id(this.namespace, path);
    }

    public void write(JsonObject object) {
        JsonObject root = new JsonObject();

        JsonArray color = new JsonArray();
        color.add(this.color.getRed());
        color.add(this.color.getGreen());
        color.add(this.color.getBlue());
        root.add("color", color);

        this.init();
        definitions.forEach((id, definition) -> {
            JsonObject serialized = definition.toJson();
            root.add(id.path(), serialized);
        });

        object.add(this.namespace, root);
    }

    public abstract void init();
}
