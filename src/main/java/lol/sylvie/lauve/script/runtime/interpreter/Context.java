package lol.sylvie.lauve.script.runtime.interpreter;

import lol.sylvie.lauve.script.runtime.script.Node;
import lol.sylvie.lauve.script.runtime.script.Script;
import lol.sylvie.lauve.util.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

// Interpreter context
public class Context {
    @Getter
    private final Script script;

    @Setter
    private Node working;

    private String loggerPrefix;

    public Context(Script script) {
        this.script = script;
        this.loggerPrefix = String.format("[%s] ", script.getFile().getName());
    }

    private final HashMap<String, Object> locals = new HashMap<>();

    public Object getLocal(String name) {
        return locals.get(name);
    }

    public void setLocal(String name, Object value) {
        locals.put(name, value);
    }

    public void log(String text, Object... args) {
        Constants.LOGGER.info(loggerPrefix + text, args);
    }

    public void warn(String text, Object... args) {
        Constants.LOGGER.warn(loggerPrefix + text, args);
    }

}
