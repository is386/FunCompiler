package cfg.primitives;

import visitor.CFGVisitor;

public class IntPrimitive extends Primitive {
    private long value;

    public IntPrimitive(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String getName() {
        return Long.toString(this.value);
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntPrimitive)) {
            return false;
        }
        IntPrimitive i = (IntPrimitive) o;
        return i.toString().equals(this.toString());
    }

    @Override
    public String toString() {
        return Long.toString(this.value);
    }
}