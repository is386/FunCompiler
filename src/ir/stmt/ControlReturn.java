package ir.stmt;

import ir.primitives.Primitive;

public class ControlReturn extends ControlStmt {
    private final Primitive value;

    public ControlReturn(Primitive value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "    ret " + value;
    }
}
