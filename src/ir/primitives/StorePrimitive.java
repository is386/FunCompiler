package ir.primitives;

public class StorePrimitive extends Primitive {
    private final Primitive location;
    private final Primitive value;

    public StorePrimitive(Primitive location, Primitive value) {
        this.location = location;
        this.value = value;
    }

    public String toString() {
        return String.format("store(%s, %s)", location, value);
    }
}
