package vn;

import java.util.HashMap;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class ValueNumbering implements CFGVisitor {

    private HashMap<String, Integer> vn = new HashMap<>();
    private HashMap<String, Integer> name = new HashMap<>();
    private Integer nextVN = 0;
    private String l;
    private String r;

    @Override
    public void visit(CFG node) {
        for (BasicBlock b : node.getBlocks()) {
            vn = new HashMap<>();
            name = new HashMap<>();
            nextVN = 0;
            b.accept(this);
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
        node.getPrimitive().accept(this);
    }

    @Override
    public void visit(ArithPrimitive node) {
        // l = node.getOperand1();
        // r = node.getOperand2();
    }

    @Override
    public void visit(CallPrimitive node) {
        return;
    }

    @Override
    public void visit(GetEltPrimitive node) {
        return;
    }

    @Override
    public void visit(IntPrimitive node) {
        return;
    }

    @Override
    public void visit(LoadPrimitive node) {
        return;
    }

    @Override
    public void visit(PhiPrimitive node) {
        return;
    }

    @Override
    public void visit(PrintPrimitive node) {
        return;
    }

    @Override
    public void visit(SetEltPrimitive node) {
        return;
    }

    @Override
    public void visit(StorePrimitive node) {
        return;
    }

    @Override
    public void visit(TempPrimitive node) {
        return;
    }

    @Override
    public void visit(ThisPrimitive node) {
        return;
    }

    @Override
    public void visit(VarPrimitive node) {
        return;
    }

    @Override
    public void visit(IRFail node) {
    }

    @Override
    public void visit(IRData node) {
    }

    @Override
    public void visit(ControlCond node) {
    }

    @Override
    public void visit(ControlReturn node) {
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

}
