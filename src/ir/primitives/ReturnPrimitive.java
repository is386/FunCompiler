package ir.primitives;

public class ReturnPrimitive extends Primitive {
    private final Primitive primitive;

    public ReturnPrimitive(Primitive p) {
        this.primitive = p;
    }

    public String toString() {
        return "ret " + primitive;
    }
}
