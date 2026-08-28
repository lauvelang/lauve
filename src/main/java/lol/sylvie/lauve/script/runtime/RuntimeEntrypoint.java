package lol.sylvie.lauve.script.runtime;

import lol.sylvie.lauve.script.operation.impl.control.LoadOperation;
import lol.sylvie.lauve.script.runtime.interpreter.Context;
import lol.sylvie.lauve.script.runtime.interpreter.Interpreter;
import lol.sylvie.lauve.script.runtime.script.Node;
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

        Context context = new Context(script);
        for (Node node : context.getScript().allNodes()) {
            if (node.operation() instanceof LoadOperation) {
                Interpreter.walk(context, node);
            }
        }
    }
}
