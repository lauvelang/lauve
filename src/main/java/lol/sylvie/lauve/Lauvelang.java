package lol.sylvie.lauve;

import lol.sylvie.lauve.error.RuntimeError;
import lol.sylvie.lauve.interpreter.Interpreter;
import lol.sylvie.lauve.parsing.*;

import java.util.HashMap;
import java.util.List;

// https://craftinginterpreters.com/scanning.html#the-scanner-class
public class Lauvelang {
    public static boolean hadError = false;
    public static boolean hadRuntimeError = false;

    public static final HashMap<String, Interpreter> INTERPRETERS = new HashMap<>();

    // ACTUALLY RUN STUFF
    public static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
                               String message) {
        Lauve.LOGGER.error("[line {}] Error{}: {}", line, where, message);
        hadError = true;
    }

    public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    public static void runtimeError(RuntimeError error) {
        Lauve.LOGGER.error("{}\n[line {}]", error.getMessage(), error.token.line);
        hadRuntimeError = true;
    }


}