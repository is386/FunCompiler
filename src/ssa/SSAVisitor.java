package ssa;

import cfg.BasicBlock;
import cfg.stmt.IRData;
import cfg.stmt.IREqual;
import cfg.stmt.IRFail;

public interface SSAVisitor {
    public void visit(BasicBlock node);

    public void visit(IREqual node);

    public void visit(IRFail node);

    public void visit(IRData node);
}
