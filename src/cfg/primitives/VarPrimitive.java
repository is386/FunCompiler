package cfg.primitives;

import cfg.Type;

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
}
