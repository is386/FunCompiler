package ir;

import ir.primitives.Primitive;

public class IRStmt {
    private Primitive var;
    private final Primitive primitive;

    public IRStmt(Primitive var, Primitive primitive) {
        this.var = var;
        this.primitive = primitive;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public void setVar(Primitive v) {
        var = v;
    }

    public String toString() {
        if (var == null) {
            return primitive.toString();
        }
        return var + " = " + primitive.toString();
    }
}
