package ssa;

import java.util.ArrayList;

import cfg.BasicBlock;
import cfg.primitives.*;
import cfg.stmt.*;

public class SSATransformer implements SSAVisitor {

    private Primitive varToReplace = null;
    private int tempVarCount = 1;

    @Override
    public void visit(ArrayList<BasicBlock> node) {
        for (BasicBlock block : node) {
            block.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        for (IRStmt ir : node.getStatements()) {
            ir.accept(this);
        }
    }

    @Override
    public void visit(IREqual node) {
        if (node.getVar() != null) {
            node.getVar().accept(this);
        }
        node.getPrimitive().accept(this);
    }

    @Override
    public void visit(ControlCond node) {
        return;
    }

    @Override
    public void visit(ControlJump node) {
        return;
    }

    @Override
    public void visit(ArithPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(CallPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(GetEltPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(LoadPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(PrintPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(SetEltPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(StorePrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(TempPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(VarPrimitive node) {
        System.out.println(node.getName());
    }

    @Override
    public void visit(IRFail node) {
    }

    @Override
    public void visit(IRData node) {
    }

    @Override
    public void visit(ControlReturn node) {
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
    public void visit(ThisPrimitive node) {
    }

    public void setTempVarCount(int i) {
        tempVarCount = i;
    }

    private TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }
}
