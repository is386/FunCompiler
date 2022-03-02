package cfg.primitives;

import visitor.CFGVisitor;

public abstract class Primitive {

    public abstract String toString();

    public String getName() {
        return "";
    }

    public abstract void accept(CFGVisitor visitor);
}
