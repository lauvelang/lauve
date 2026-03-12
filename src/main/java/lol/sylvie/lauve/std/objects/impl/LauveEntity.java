package lol.sylvie.lauve.std.objects.impl;

import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.std.objects.NativeLauveClass;
import net.minecraft.world.entity.Entity;

import java.util.Map;

public class LauveEntity extends NativeLauveClass<Entity> {
    public LauveEntity(String name, Map<String, LauveFunction> methods, Class<Entity> clazz) {
        super(name, null, methods, clazz);
    }
}
