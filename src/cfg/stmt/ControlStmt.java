package cfg.stmt;

import java.util.ArrayList;

import ssa.SSAVisitor;

public abstract class ControlStmt {
    public abstract String toString();

    public abstract ArrayList<String> getBranchNames();

    public abstract void accept(SSAVisitor visitor);
}
