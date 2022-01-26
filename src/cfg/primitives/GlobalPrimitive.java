package cfg.primitives;

import visitor.CFGVisitor;

public class GlobalPrimitive extends Primitive {
    private String name;

    public GlobalPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "@" + name;
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
