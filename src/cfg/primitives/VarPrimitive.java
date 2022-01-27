package cfg.primitives;

import cfg.Type;
import visitor.CFGVisitor;

public class VarPrimitive extends Primitive {
    protected String name;
    private Type type = Type.UNDECLARED;
    private int version = 0;

    public VarPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name + version;
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

    public void setName(String n) {
        name = n;
    }

    public void setVersion(int v) {
        version = v;
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
