package ssa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class SSATransformer implements CFGVisitor {

    private ArrayList<VarPrimitive> currentBlockVars = new ArrayList<>();
    private HashSet<BasicBlock> visited = new HashSet<>();
    private CFG cfg;

    @Override
    public void visit(CFG node) {
        cfg = node;
        for (BasicBlock block : node.getBlocks()) {
            block.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        if (node.getParents().size() >= 2 && node.getControlStmt() != null) {
            visited = new HashSet<>();

            for (IRStmt ir : node.getStatements()) {
                ir.accept(this);
            }
            node.getControlStmt().accept(this);

            for (VarPrimitive v : currentBlockVars) {
                LinkedHashMap<String, Primitive> varMap = new LinkedHashMap<>();

                for (BasicBlock p : node.getParents()) {
                    TempPrimitive temp = cfg.getNextTemp();
                    PhiReplacer phiReplacer = new PhiReplacer(v, temp);
                    String pName = findLastVersion(phiReplacer, p);
                    if (pName != null) {
                        varMap.put(p.getName(), temp);
                    } else {
                        cfg.decrementTempVarCount();
                    }
                }

                PhiPrimitive phi = new PhiPrimitive();
                phi.setVarMap(varMap);
                IREqual ir = new IREqual(v, phi);
                node.insertStart(ir);
            }

            currentBlockVars = new ArrayList<>();
        }
    }

    public String findLastVersion(PhiReplacer phi, BasicBlock block) {
        String blockName;
        visited.add(block);
        block.accept(phi);
        if (phi.isReplaced()) {
            return block.getName();
        } else if (block.getParents().size() == 0) {
            return null;
        } else {
            for (BasicBlock p : block.getParents()) {
                if (visited.contains(p)) {
                    continue;
                }
                blockName = findLastVersion(phi, p);
                if (blockName != null) {
                    return blockName;
                }
            }
        }
        return null;
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
}
