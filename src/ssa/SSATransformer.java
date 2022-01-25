package ssa;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cfg.BasicBlock;
import cfg.primitives.*;
import cfg.stmt.*;

public class SSATransformer implements SSAVisitor {

    private int tempVarCount = 1;
    private ArrayList<VarPrimitive> currentBlockVars = new ArrayList<>();

    @Override
    public void visit(ArrayList<BasicBlock> node) {
        for (BasicBlock block : node) {
            block.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        if (node.getParents().size() >= 2 && node.getControlStmt() != null) {
            for (IRStmt ir : node.getStatements()) {
                ir.accept(this);
            }
            node.getControlStmt().accept(this);

            for (VarPrimitive v : currentBlockVars) {
                LinkedHashMap<String, Primitive> varMap = new LinkedHashMap<>();
                for (BasicBlock p : node.getParents()) {
                    TempPrimitive temp = getNextTemp();
                    varMap.put(p.getName(), temp);
                    PhiReplacer phiReplacer = new PhiReplacer(v, temp);
                    p.accept(phiReplacer);
                }
                PhiPrimitive phi = new PhiPrimitive();
                phi.setVarMap(varMap);
                IREqual ir = new IREqual(v, phi);
                node.insertStart(ir);
            }

            currentBlockVars = new ArrayList<>();
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
        currentBlockVars.add(node);
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

    public void setTempVarCount(int i) {
        tempVarCount = i;
    }

    private TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }
}
