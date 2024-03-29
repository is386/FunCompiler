package visitor;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.AllocPrimitive;
import cfg.primitives.ArithPrimitive;
import cfg.primitives.CallPrimitive;
import cfg.primitives.GetEltPrimitive;
import cfg.primitives.GlobalPrimitive;
import cfg.primitives.IntPrimitive;
import cfg.primitives.LoadPrimitive;
import cfg.primitives.PhiPrimitive;
import cfg.primitives.PrintPrimitive;
import cfg.primitives.SetEltPrimitive;
import cfg.primitives.StorePrimitive;
import cfg.primitives.TempPrimitive;
import cfg.primitives.ThisPrimitive;
import cfg.primitives.VarPrimitive;
import cfg.stmt.ControlCond;
import cfg.stmt.ControlJump;
import cfg.stmt.ControlReturn;
import cfg.stmt.IRData;
import cfg.stmt.IREqual;
import cfg.stmt.IRFail;

public interface CFGVisitor {

    public void visit(CFG node);

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
