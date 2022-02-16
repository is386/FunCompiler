package cfg.stmt;

import java.util.ArrayList;

import cfg.primitives.Primitive;
import visitor.CFGVisitor;

public class ControlCond extends ControlStmt {
    private Primitive cond;
    private final String ifBranch;
    private final String elseBranch;

    public ControlCond(Primitive cond, String ifBranch, String elseBranch) {
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    public Primitive getCond() {
        return cond;
    }

    public void setCond(Primitive c) {
        cond = c;
    }

    public String getIf() {
        return ifBranch;
    }

    public String getElse() {
        return elseBranch;
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
