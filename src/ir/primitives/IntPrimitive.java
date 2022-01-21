package ir.primitives;

import ast.expr.Type;

public class IntPrimitive extends Primitive {
    private int value;

    public IntPrimitive(int value, boolean tagged) {
        this.value = tagged ? (value << 1) + 1 : value;
    }

    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}