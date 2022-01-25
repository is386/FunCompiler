package cfg.primitives;

import ssa.SSAVisitor;

public class LoadPrimitive extends Primitive {
    private final Primitive primitive;

    public LoadPrimitive(Primitive p) {
        this.primitive = p;
    }

    public String toString() {
        return "load(" + primitive + ")";
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
