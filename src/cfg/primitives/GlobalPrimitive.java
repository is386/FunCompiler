package cfg.primitives;

import ssa.SSAVisitor;

public class GlobalPrimitive extends Primitive {
    private String name;

    public GlobalPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "@" + name;
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
