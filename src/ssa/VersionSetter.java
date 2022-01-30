package ssa;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class VersionSetter implements CFGVisitor {

    private String var;
    private int rightVersion;
    private int leftVersion;
    private boolean isEqualStmt = false;
    private boolean isAssigned = false;

    public VersionSetter(String var, int rightVersion, int leftVersion) {
        this.var = var;
        this.rightVersion = rightVersion;
        this.leftVersion = leftVersion;
    }

    @Override
    public void visit(BasicBlock node) {
        for (IRStmt ir : node.getStatements()) {
            ir.accept(this);
        }
        if (node.getControlStmt() != null) {
            node.getControlStmt().accept(this);
        }
    }

    @Override
    public void visit(IREqual node) {
        if (node.getVar() != null) {
            isEqualStmt = true;
            node.getVar().accept(this);
        }
        isEqualStmt = false;
        node.getPrimitive().accept(this);
        if (isAssigned) {
            rightVersion = leftVersion;
        }
    }

    @Override
    public void visit(ControlCond node) {
        node.getCond().accept(this);
    }

    @Override
    public void visit(ControlReturn node) {
        node.getValue().accept(this);
    }

    @Override
    public void visit(ArithPrimitive node) {
        node.getOperand1().accept(this);
        node.getOperand2().accept(this);
    }

    @Override
    public void visit(CallPrimitive node) {
        node.getCaller().accept(this);
        node.getCodeAddress().accept(this);
        for (Primitive a : node.getArgs()) {
            a.accept(this);
        }
    }

    @Override
    public void visit(GetEltPrimitive node) {
        node.getOffset().accept(this);
        node.getPrimitive().accept(this);
    }

    @Override
    public void visit(LoadPrimitive node) {
        node.getPrimitive().accept(this);
    }

    @Override
    public void visit(PrintPrimitive node) {
        node.getPrimitive().accept(this);
    }

    @Override
    public void visit(SetEltPrimitive node) {
        node.getVar().accept(this);
        node.getValue().accept(this);
        node.getLocation().accept(this);
    }

    @Override
    public void visit(StorePrimitive node) {
        node.getLocation().accept(this);
        node.getValue().accept(this);
    }

    @Override
    public void visit(VarPrimitive node) {
        if (isEqualStmt && node.getName().equals(var)) {
            node.setVersion(leftVersion);
            isAssigned = true;
        } else if (node.getName().equals(var)) {
            node.setVersion(rightVersion);
        }
    }

    @Override
    public void visit(CFG node) {
    }

    @Override
    public void visit(IRFail node) {
    }

    @Override
    public void visit(IRData node) {
    }

    @Override
    public void visit(ControlJump node) {
    }

    @Override
    public void visit(AllocPrimitive node) {
    }

    @Override
    public void visit(GlobalPrimitive node) {
    }

    @Override
    public void visit(IntPrimitive node) {
    }

    @Override
    public void visit(PhiPrimitive node) {
    }

    @Override
    public void visit(TempPrimitive node) {
    }

    @Override
    public void visit(ThisPrimitive node) {
    }
}
