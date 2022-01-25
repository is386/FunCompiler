package ssa;

import java.util.ArrayList;

import cfg.BasicBlock;
import cfg.primitives.*;
import cfg.stmt.*;

public interface SSAVisitor {

    public void visit(ArrayList<BasicBlock> node);

    public void visit(BasicBlock node);

    public void visit(IREqual node);

    public void visit(IRFail node);

    public void visit(IRData node);

    public void visit(ControlCond node);

    public void visit(ControlJump node);

    public void visit(ControlReturn node);

    public void visit(AllocPrimitive node);

    public void visit(ArithPrimitive node);

    public void visit(CallPrimitive node);

    public void visit(GetEltPrimitive node);

    public void visit(GlobalPrimitive node);

    public void visit(IntPrimitive node);

    public void visit(LoadPrimitive node);

    public void visit(PhiPrimitive node);

    public void visit(PrintPrimitive node);

    public void visit(SetEltPrimitive node);

    public void visit(StorePrimitive node);

    public void visit(TempPrimitive node);

    public void visit(ThisPrimitive node);

    public void visit(VarPrimitive node);
}
