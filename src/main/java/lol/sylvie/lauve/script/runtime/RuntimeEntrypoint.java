package lol.sylvie.lauve.script.runtime;

import lol.sylvie.lauve.script.runtime.script.Script;

import java.io.File;

public class RuntimeEntrypoint {
    static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: [script file]");
            System.exit(-1);
        }

        String path = args[0];
        Script script = new Script(new File(path));
        script.load();
        script.run();
    }
}
