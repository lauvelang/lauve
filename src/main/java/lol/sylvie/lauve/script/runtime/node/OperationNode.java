package lol.sylvie.lauve.script.runtime.node;

import lol.sylvie.lauve.script.operation.Operation;

import java.util.HashMap;
import java.util.UUID;

public record OperationNode(UUID rid, Operation operation, HashMap<String, InputNode> args, OperationNode parent, OperationNode next) {}
