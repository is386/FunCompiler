package cfg.primitives;

import cfg.Type;
import ssa.SSAVisitor;

public class VarPrimitive extends Primitive {
    private String name;
    private Type type = Type.UNDECLARED;

    public VarPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        type = t;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
