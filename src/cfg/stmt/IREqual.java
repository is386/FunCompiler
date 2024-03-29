package cfg.stmt;

import cfg.primitives.Primitive;
import visitor.CFGVisitor;

public class IREqual extends IRStmt {
    private Primitive var;
    private Primitive primitive;

    public IREqual(Primitive var, Primitive primitive) {
        this.var = var;
        this.primitive = primitive;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public void setPrimitive(Primitive p) {
        primitive = p;
    }

    public Primitive getVar() {
        return var;
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
