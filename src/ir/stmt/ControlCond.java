package ir.stmt;

import ir.primitives.Primitive;

public class ControlCond extends ControlStmt {
    private final Primitive cond;
    private final String ifBranch;
    private final String elseBranch;

    public ControlCond(Primitive cond, String ifBranch, String elseBranch) {
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public String toString() {
        return "    if " + cond + " then " + ifBranch + " else " + elseBranch;
    }
}
