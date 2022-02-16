package cfg.primitives;

import visitor.CFGVisitor;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "%this0";
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ThisPrimitive)) {
            return false;
        }
        ThisPrimitive i = (ThisPrimitive) o;
        return i.getName().equals(this.name);
    }
}
