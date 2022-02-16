package cfg.primitives;

import visitor.CFGVisitor;

public class StorePrimitive extends Primitive {
    private Primitive location;
    private Primitive value;

    public StorePrimitive(Primitive location, Primitive value) {
        this.location = location;
        this.value = value;
    }

    public void setLocation(Primitive location) {
        this.location = location;
    }

    public void setValue(Primitive value) {
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
