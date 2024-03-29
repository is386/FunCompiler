package cfg.primitives;

import visitor.CFGVisitor;

public class LoadPrimitive extends Primitive {
    private Primitive primitive;

    public LoadPrimitive(Primitive p) {
        this.primitive = p;
    }

    public void setPrimitive(Primitive p) {
        primitive = p;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public String toString() {
        return "load(" + primitive + ")";
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
