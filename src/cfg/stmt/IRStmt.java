package cfg.stmt;

import ssa.SSAVisitor;

public abstract class IRStmt {
    public abstract String toString();

    public abstract void accept(SSAVisitor visitor);
}
