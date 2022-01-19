package ir.primitives;

public class IntPrimitive extends Primitive {
    private int value;

    public IntPrimitive(int value) {
        this.value = value;
    }

    public String toString() {
        return Integer.toString(value);
    }
}
