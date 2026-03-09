package lol.sylvie.lauve;

import lol.sylvie.lauve.command.LauveCommand;
import lol.sylvie.lauve.interpreter.Interpreter;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Lauve implements ModInitializer {
	public static final String MOD_ID = "lauve";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Path SCRIPTS = FabricLoader.getInstance().getGameDir().resolve("scripts");

	@Override
	public void onInitialize() {
		if (!Files.exists(SCRIPTS)) {
			SCRIPTS.toFile().mkdir();
		};

		LauveCommand.register();
	}
}