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

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Id other)) return false;
        return other.namespace.equals(this.namespace) && other.path.equals(this.path);
    }
}
