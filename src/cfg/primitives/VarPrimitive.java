package cfg.primitives;

import visitor.CFGVisitor;

public class VarPrimitive extends Primitive {
    protected String name;
    private int version = 0;

    public VarPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name + version;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int v) {
        version = v;
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VarPrimitive)) {
            return false;
        }
        VarPrimitive i = (VarPrimitive) o;
        return i.getName().equals(this.name) && i.getVersion() == this.version;
    }
}
