package ir.primitives;

import ir.Type;

public class IntPrimitive extends Primitive {
    private long value;

    public IntPrimitive(int value, boolean tagged) {
        this.value = tagged ? (value << 1) + 1 : value;
    }

    public IntPrimitive(long value) {
        this.value = value;
    }

    public String toString() {
        return Long.toString(value);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}