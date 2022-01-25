package ssa;

import java.util.ArrayList;

import cfg.BasicBlock;
import cfg.primitives.*;
import cfg.stmt.*;

public class PhiReplacer implements SSAVisitor {

    private VarPrimitive var;
    private TempPrimitive newVar;

    public PhiReplacer(VarPrimitive var, TempPrimitive newVar) {
        this.var = var;
        this.newVar = newVar;
    }

    @Override
    public void visit(BasicBlock node) {
    }

    @Override
    public void visit(IREqual node) {
    }

    @Override
    public void visit(ControlCond node) {
    }

    @Override
    public void visit(ControlReturn node) {
    }

    @Override
    public void visit(ArithPrimitive node) {
    }

    @Override
    public void visit(CallPrimitive node) {
    }

    @Override
    public void visit(GetEltPrimitive node) {
    }

    @Override
    public void visit(LoadPrimitive node) {
    }

    @Override
    public void visit(PrintPrimitive node) {
    }

    @Override
    public void visit(SetEltPrimitive node) {
    }

    @Override
    public void visit(StorePrimitive node) {
    }

    // Do Nothing

    @Override
    public void visit(ArrayList<BasicBlock> node) {
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

    @Override
    public void visit(VarPrimitive node) {
    }
}
