package cfg.stmt;

import cfg.primitives.Primitive;
import ssa.SSAVisitor;

public class IREqual extends IRStmt {
    private Primitive var;
    private final Primitive primitive;

    public IREqual(Primitive var, Primitive primitive) {
        this.var = var;
        this.primitive = primitive;
        if (var != null) {
            var.setType(primitive.getType());
        }
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

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
