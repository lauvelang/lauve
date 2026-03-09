package lol.sylvie.lauve.std.objects.impl;

import lol.sylvie.lauve.parsing.LauveFunction;
import lol.sylvie.lauve.std.objects.NativeLauveClass;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class LauveServerPlayer extends NativeLauveClass<ServerPlayer> {
    public LauveServerPlayer(String name, Map<String, LauveFunction> methods) {
        super(name, methods, ServerPlayer.class);
    }
}
