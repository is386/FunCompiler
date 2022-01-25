package cfg.primitives;

import cfg.Type;
import ssa.SSAVisitor;

public abstract class Primitive {
    private Type type = Type.UNDECLARED;

    public abstract String toString();

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    public String getName() {
        return "";
    }

    public abstract void accept(SSAVisitor visitor);
}
