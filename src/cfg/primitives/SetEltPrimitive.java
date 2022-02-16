package cfg.primitives;

import visitor.CFGVisitor;

public class SetEltPrimitive extends Primitive {
    private Primitive var;
    private Primitive location;
    private Primitive value;

    public SetEltPrimitive(Primitive var, Primitive location, Primitive value) {
        this.var = var;
        this.location = location;
        this.value = value;
    }

    public void setVar(Primitive var) {
        this.var = var;
    }

    public void setLocation(Primitive location) {
        this.location = location;
    }

    public void setValue(Primitive value) {
        this.value = value;
    }

    public Primitive getVar() {
        return var;
    }

    public Primitive getLocation() {
        return location;
    }

    public Primitive getValue() {
        return value;
    }

    public String toString() {
        return String.format("setelt(%s, %s, %s)", var, location, value);
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
