package lol.sylvie.lauve.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import lol.sylvie.lauve.Lauve;
import lol.sylvie.lauve.Lauvelang;
import lol.sylvie.lauve.interpreter.Interpreter;
import lol.sylvie.lauve.parsing.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LauveCommand {
    private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(error -> Component.literal("Script not found: " + error));
    private static final DynamicCommandExceptionType ERROR_WHILE_PARSING = new DynamicCommandExceptionType(error -> Component.literal("Couldn't parse script: " + error));
    private static final DynamicCommandExceptionType ERROR_WHILE_RESOLVING = new DynamicCommandExceptionType(error -> Component.literal("Couldn't resolve script: " + error));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Lauve.MOD_ID);

            LiteralArgumentBuilder<CommandSourceStack> load = Commands.literal("load");
            RequiredArgumentBuilder<CommandSourceStack, String> scriptArg = Commands.argument("script", StringArgumentType.greedyString());

            scriptArg.executes(context -> {
                String filename = context.getArgument("script", String.class);
                Path script = Lauve.SCRIPTS.resolve(filename);

                if (!Files.isRegularFile(script)) {
                    throw NOT_FOUND.create(filename);
                }

                String key = script.toAbsolutePath().toString();
                if (Lauvelang.INTERPRETERS.containsKey(key)) {
                    context.getSource().sendSuccess(() -> Component.literal("Unloading previous version of the script..."), false);
                    Interpreter lastInterpreter = Lauvelang.INTERPRETERS.get(key);
                    lastInterpreter.cleanup();
                    Lauvelang.INTERPRETERS.remove(key);
                }

                context.getSource().sendSuccess(() -> Component.literal("Loading file " + filename + "..."), false);
                String source;
                try {
                    source = Files.readString(script);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Lauvelang.hadError = false;

                Scanner scanner = new Scanner(source);
                List<Token> tokens = scanner.scanTokens();

                Parser parser = new Parser(tokens);
                List<Stmt> statements = parser.parse();

                if (Lauvelang.hadError)
                    throw ERROR_WHILE_PARSING.create(source);

                String name = script.getFileName().toString().split("\\.")[0];
                Interpreter interpreter = new Interpreter(name);
                Resolver resolver = new Resolver(interpreter);
                resolver.resolve(statements);

                if (Lauvelang.hadError)
                    throw ERROR_WHILE_RESOLVING.create(source);

                Lauvelang.INTERPRETERS.put(key, interpreter);
                interpreter.interpret(statements);

                if (interpreter.listeners.isEmpty()) {
                    context.getSource().sendSuccess(() -> Component.literal("Loaded script " + filename + ", but there are no events in the file."), false);
                    return 1;
                }

                context.getSource().sendSuccess(() -> Component.literal("Successfully loaded script!"), false);

                return 0;
            });

            load.then(scriptArg);
            root.then(load);

            dispatcher.register(root);
        });
    }
}
