package ir.primitives;

public class PrintPrimitive extends Primitive {
    private final Primitive primitive;

    public PrintPrimitive(Primitive p) {
        this.primitive = p;
    }

    public String toString() {
        return "print(" + primitive + ")";
    }
}
