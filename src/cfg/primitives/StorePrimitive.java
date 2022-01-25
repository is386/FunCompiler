package cfg.primitives;

import ssa.SSAVisitor;

public class StorePrimitive extends Primitive {
    private final Primitive location;
    private final Primitive value;

    public StorePrimitive(Primitive location, Primitive value) {
        this.location = location;
        this.value = value;
    }

    public Primitive getLocation() {
        return location;
    }

    public Primitive getValue() {
        return value;
    }

    public String toString() {
        return String.format("store(%s, %s)", location, value);
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}