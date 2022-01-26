package cfg.primitives;

import visitor.CFGVisitor;

public class PrintPrimitive extends Primitive {
    private final Primitive primitive;

    public PrintPrimitive(Primitive p) {
        this.primitive = p;
    }

    public Primitive getPrimitive() {
        return primitive;
    }

    public String toString() {
        return "print(" + primitive + ")";
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
