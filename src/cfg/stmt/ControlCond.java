package cfg.stmt;

import java.util.ArrayList;

import cfg.primitives.Primitive;
import ssa.SSAVisitor;

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

    @Override
    public ArrayList<String> getBranchNames() {
        ArrayList<String> a = new ArrayList<>();
        a.add(ifBranch);
        a.add(elseBranch);
        return a;
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
