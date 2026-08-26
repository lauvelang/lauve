package lol.sylvie.lauve.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

// Minecraft has its own Identifier
// but a separate object will help keep the
// Minecraft specifics and the Lauve specifics separate
public record Id(String namespace, String path) {
    private static final String separator = ":";

    @Override
    public @NotNull String toString() {
        return namespace + separator + path;
    }

    public Id(String string) {
        String[] split = string.split(separator);
        if (split.length != 2) throw new IllegalArgumentException("Correct format for ID is namespace:path");

        this(split[0], split[1]);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Id(String namespace2, String path2))) return false;
        return namespace2.equals(this.namespace) && path2.equals(this.path);
    }
}
