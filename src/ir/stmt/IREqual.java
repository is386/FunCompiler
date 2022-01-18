package ir.stmt;

import ir.primitives.Primitive;
import ir.primitives.VariablePrimitive;

public class IREqual extends IRStmt {

    private final VariablePrimitive var;
    private final Primitive primitive;

    public IREqual(VariablePrimitive var, Primitive p) {
        this.var = var;
        this.primitive = p;
    }

    public String toString() {
        return var.toString() + " = " + primitive.toString();
    }

}
