package lol.sylvie.lauve.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class IdentifiedObject {
    @Getter
    private final Id id;

    protected IdentifiedObject(String name) {
        this.id = new Id(Reflection.getParentPackage(this.getClass()), name);
    }
}
