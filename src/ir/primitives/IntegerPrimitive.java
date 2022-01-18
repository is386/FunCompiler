package ir.primitives;

public class IntegerPrimitive extends Primitive {

    private final int value;

    public IntegerPrimitive(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Primitive getVar() {
        return this;
    }
}
