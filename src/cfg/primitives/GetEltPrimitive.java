package cfg.primitives;

import ssa.SSAVisitor;

public class GetEltPrimitive extends Primitive {
    private final Primitive primitive;
    private final Primitive offset;

    public GetEltPrimitive(Primitive p, Primitive offset) {
        this.primitive = p;
        this.offset = offset;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public Primitive getOffset() {
        return offset;
    }

    public String toString() {
        return String.format("getelt(%s, %s)", primitive, offset);
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
