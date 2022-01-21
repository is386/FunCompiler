package ir.primitives;

import ast.expr.Type;

public class IntPrimitive extends Primitive {
    private int value;

    public IntPrimitive(int value) {
        this.value = value;
    }

    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }
}