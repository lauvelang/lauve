package lol.sylvie.lauve.util;

import lol.sylvie.lauve.script.runtime.Context;

public class ScriptException extends RuntimeException {
    private final Context context;

    public ScriptException(Context context, String message) {
        super(message);
        this.context = context;
    }
}
