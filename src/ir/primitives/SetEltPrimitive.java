package ir.primitives;

public class SetEltPrimitive extends Primitive {
    private final Primitive var;
    private final Primitive location;
    private final Primitive value;

    public SetEltPrimitive(Primitive var, Primitive location, Primitive value) {
        this.var = var;
        this.location = location;
        this.value = value;
    }

    public String toString() {
        return String.format("setelt(%s, %s, %s)", var, location, value);
    }
}
