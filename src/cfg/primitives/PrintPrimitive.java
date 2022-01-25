package cfg.primitives;

import ssa.SSAVisitor;

public class PrintPrimitive extends Primitive {
    private final Primitive primitive;

    public PrintPrimitive(Primitive p) {
        this.primitive = p;
    }

    public String toString() {
        return "print(" + primitive + ")";
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
