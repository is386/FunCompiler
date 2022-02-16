package cfg.primitives;

import visitor.CFGVisitor;

public class TempPrimitive extends VarPrimitive {
    public TempPrimitive(String name) {
        super(name);
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "%" + name;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TempPrimitive)) {
            return false;
        }
        TempPrimitive i = (TempPrimitive) o;
        return i.getName().equals(this.name);
    }
}
