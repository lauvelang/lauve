package lol.sylvie.lauve.script.datagen;

import com.google.gson.JsonObject;
import lol.sylvie.lauve.script.datagen.definition.Definition;
import lol.sylvie.lauve.util.Constants;
import lol.sylvie.lauve.util.Id;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public abstract class BlockGroup {
    private final HashMap<Id, Definition> definitions = new HashMap<>();

    private final String namespace;
    protected final File folder;

    protected BlockGroup(String namespace) {
        this.namespace = namespace;

        this.folder = new File("./" + this.namespace);
    }

    protected static void toFile(File file, JsonSerializable serializable) {
        try (FileWriter writer = new FileWriter(file)) {
            JsonObject object = serializable.toJson();
            Constants.GSON.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void define(Definition definition) {
        this.definitions.put(definition.getDescribes(), definition);
    }

    protected Id id(String path) {
        return new Id(this.namespace, path);
    }

    public void write() {
        if (!(this.folder.exists())) {
            this.folder.mkdir();
        }

        this.init();
        definitions.forEach((id, definition) -> {
            File file = folder.toPath().resolve(id.path() + ".json").toFile();
            toFile(file, definition);
        });
    }

    public abstract void init();
}
