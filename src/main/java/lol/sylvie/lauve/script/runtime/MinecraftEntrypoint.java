package lol.sylvie.lauve.script.runtime;

import lol.sylvie.lauve.script.runtime.script.Script;
import lombok.SneakyThrows;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class MinecraftEntrypoint {
    @SneakyThrows
    public static void init() {
        Path folder = FabricLoader.getInstance().getGameDir().resolve("lauve");
        if (!Files.exists(folder)) Files.createDirectory(folder);

        for (Path path : Files.list(folder).toList()) {
            Script script = new Script(path.toFile());
            script.load();

            new Thread(script::run).start();
        }
    }
}
