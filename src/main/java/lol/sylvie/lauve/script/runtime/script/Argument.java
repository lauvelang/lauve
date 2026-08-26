package lol.sylvie.lauve.script.runtime.script;

import java.util.UUID;

public record Argument(Object computed, UUID references) {
    public Argument(Object computed) {
        this(computed, null);
    }

    public Argument(UUID references) {
        this(null, references);
    }

    public boolean isComputed() {
        return this.computed != null;
    }

}
