package cfg.primitives;

import ssa.SSAVisitor;

public class AllocPrimitive extends Primitive {
    private final int space;

    public AllocPrimitive(int space) {
        this.space = space;
    }

    public String toString() {
        return "alloc(" + Integer.toString(space) + ")";
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
