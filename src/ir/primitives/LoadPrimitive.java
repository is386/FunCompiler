package ir.primitives;

public class LoadPrimitive extends Primitive {
    private final Primitive primitive;

    public LoadPrimitive(Primitive p) {
        this.primitive = p;
    }

    public String toString() {
        return "load(" + primitive + ")";
    }
}
