package lol.sylvie.lauve.script.runtime.node;

import lol.sylvie.lauve.script.input.Input;

import java.util.HashMap;
import java.util.UUID;

public record InputNode(UUID rid, Input input, HashMap<String, Object> args) {}
