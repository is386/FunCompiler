package cfg.primitives;

import cfg.Type;
import visitor.CFGVisitor;

public class IntPrimitive extends Primitive {
    private long value;
    private String strValue;

    public IntPrimitive(int value, boolean tagged) {
        this.value = tagged ? (value << 1) + 1 : value;
        this.strValue = Long.toString(this.value);
    }

    public IntPrimitive(String value) {
        this.strValue = value;
    }

    public String toString() {
        return strValue;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String getName() {
        return strValue;
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
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
        return i.getValue() == this.getValue();
    }
}