package cfg.stmt;

import visitor.CFGVisitor;

public abstract class IRStmt {
    public abstract String toString();

    public abstract void accept(CFGVisitor visitor);
}
