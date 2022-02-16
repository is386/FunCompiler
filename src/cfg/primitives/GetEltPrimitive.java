package cfg.primitives;

import visitor.CFGVisitor;

public class GetEltPrimitive extends Primitive {
    private Primitive primitive;
    private Primitive offset;

    public GetEltPrimitive(Primitive p, Primitive offset) {
        this.primitive = p;
        this.offset = offset;
    }

    public void setPrimitive(Primitive primitive) {
        this.primitive = primitive;
    }

    public void setOffset(Primitive offset) {
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
