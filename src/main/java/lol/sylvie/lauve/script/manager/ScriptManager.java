package lol.sylvie.lauve.script.manager;

import java.util.HashMap;

public class ScriptManager {
    private static HashMap<String, Object> globals = new HashMap<>();

    public static Object getGlobal(String name) {
        return globals.get(name);
    }

    public static void setGlobal(String name, Object value) {
        globals.put(name, value);
    }
}
