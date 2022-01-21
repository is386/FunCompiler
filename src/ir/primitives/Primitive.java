package ir.primitives;

import ast.expr.Type;

public abstract class Primitive {
    private Type type = Type.UNDECLARED;

    public abstract String toString();

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }
}
