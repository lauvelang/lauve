package lol.sylvie.lauve.std.objects;

import lol.sylvie.lauve.parsing.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NativeClasses {
    private static final Map<Class<?>, NativeLauveClass<?>> instances = new HashMap<>();

    public static final NativeLauveClass<Vec3i> VEC3I = register(new NativeLauveClass<>(Vec3i.class, null, Map.of(
            //"compareTo", new LauveFunction(new Stmt.Function(new Token(TokenType.IDENTIFIER, "compareTo", null, -1), ))
    ), Set.of("x", "y", "z")));

    /*public static final NativeLauveClass<BlockPos> BLOCKPOS = register(new NativeLauveClass<>(BlockPos.class, VEC3I, Map.of(

    ), Set.of()));

    public static final NativeLauveClass<Entity> ENTITY = register(new NativeLauveClass<>(Entity.class, null, Map.of(

    ), Map.of(

    )));*/



    private static <T> NativeLauveClass<T> register(NativeLauveClass<T> clazz) {
        instances.put(clazz.getBackingClass(), clazz);
        return clazz;
    }

    public static LauveInstance getLauveInstance(Object object) {
        return null;
    }
}
