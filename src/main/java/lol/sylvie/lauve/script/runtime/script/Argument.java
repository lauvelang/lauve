package lol.sylvie.lauve.script.runtime.script;

public record Argument(Object computed, String references) {
    public Argument(Object computed) {
        this(computed, null);
    }

    public Argument(String references) {
        this(null, references);
    }

    public boolean isComputed() {
        return this.computed != null;
    }

}
