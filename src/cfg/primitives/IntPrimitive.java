package cfg.primitives;

import cfg.Type;
import ssa.SSAVisitor;

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

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}