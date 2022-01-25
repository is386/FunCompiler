package ssa;

import cfg.BasicBlock;
import cfg.stmt.IRData;
import cfg.stmt.IREqual;
import cfg.stmt.IRFail;

public class SSATransformer implements SSAVisitor {

    @Override
    public void visit(BasicBlock node) {
        return;
    }

    @Override
    public void visit(IREqual node) {
        return;
    }

    @Override
    public void visit(IRFail node) {
        return;
    }

    @Override
    public void visit(IRData node) {
        return;
    }

}
