package ssa;

import java.util.*;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class SSAOptimized implements CFGVisitor {

    private CFG cfg;
    private BasicBlock currentBlock;
    private boolean isEqualStmt = false;
    private LinkedHashMap<String, HashMap<String, Integer>> lVersionMap = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<String, Integer>> rVersionMap = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> varCount = new LinkedHashMap<>();
    private LinkedHashMap<String, HashSet<BasicBlock>> blocksByAssignedVar = new LinkedHashMap<>();
    private LinkedHashMap<String, HashSet<BasicBlock>> phiBlocks = new LinkedHashMap<>();
    private HashSet<String> currentBlocks = new HashSet<>();

    @Override
    public void visit(CFG node) {
        cfg = node;
        reset();

        for (int i = 0; i < cfg.getNumFuncs(); i++) {
            for (String v : cfg.getFuncVars(i)) {
                varCount.put(v, 0);
            }
            currentBlocks = cfg.getFuncBlocks(i);
            setVersions(cfg.getBlocks());
            reset();
        }

    }

    public void reset() {
        varCount = new LinkedHashMap<>();
        lVersionMap = new LinkedHashMap<>();
        rVersionMap = new LinkedHashMap<>();
        phiBlocks = new LinkedHashMap<>();
        blocksByAssignedVar = new LinkedHashMap<>();
        currentBlocks = new HashSet<>();
        isEqualStmt = false;
        currentBlock = null;
    }

    public void setVersions(ArrayList<BasicBlock> blocks) {
        for (BasicBlock b : blocks) {
            if (b.getControlStmt() == null || !currentBlocks.contains(b.getName())) {
                continue;
            }
            // if (b.getParents().size() >= 2) {
            // phiBlocks.add(b);
            // }

            currentBlock = b;
            lVersionMap.put(b.getName(), new LinkedHashMap<>());
            rVersionMap.put(b.getName(), new LinkedHashMap<>());
            b.accept(this);

            for (String v : varCount.keySet()) {
                if (lVersionMap.get(b.getName()).containsKey(v)) {
                    continue;
                }
                if (b.getParents().size() == 1) {
                    Integer val = lVersionMap.get(b.getParents().get(0).getName()).get(v);
                    lVersionMap.get(b.getName()).put(v, val);
                } else if (b.getParents().size() == 0) {
                    lVersionMap.get(b.getName()).put(v, 0);
                } else {
                    lVersionMap.get(b.getName()).put(v, varCount.get(v) + 1);
                    varCount.put(v, varCount.get(v) + 1);
                }
                if (rVersionMap.get(b.getName()).containsKey(v)) {
                    continue;
                }
                if (b.getParents().size() == 1) {
                    Integer val = lVersionMap.get(b.getParents().get(0).getName()).get(v);
                    rVersionMap.get(b.getName()).put(v, val);
                } else if (b.getParents().size() == 0) {
                    rVersionMap.get(b.getName()).put(v, 0);
                } else {
                    rVersionMap.get(b.getName()).put(v, varCount.get(v));
                }
            }
        }

        for (String var : blocksByAssignedVar.keySet()) {
            phiBlocks.put(var, new HashSet<>());
        }

        boolean hasPhiBlocks = false;
        for (String var : phiBlocks.keySet()) {
            if (blocksByAssignedVar.get(var).size() < 2) {
                continue;
            }
            for (BasicBlock b : blocksByAssignedVar.get(var)) {
                for (BasicBlock d : b.getDF()) {
                    if (!phiBlocks.get(var).contains(d) && !d.isFail()) {
                        hasPhiBlocks = true;
                        phiBlocks.get(var).add(d);
                    }
                }
            }
        }

        if (!hasPhiBlocks) {
            return;
        }

        // for (String var : blocksByAssignedVar.keySet()) {
        // System.out.print(var + ":");
        // for (BasicBlock b : blocksByAssignedVar.get(var)) {
        // System.out.print(" " + b.getName());
        // }
        // System.out.print("\n");
        // }
        // System.out.print("\n");

        // for (String v : phiBlocks.keySet()) {
        // System.out.print(v + ":");
        // for (BasicBlock b : phiBlocks.get(v)) {
        // System.out.print(" " + b.getName());
        // }
        // System.out.print("\n");
        // }

        for (String v : phiBlocks.keySet()) {
            for (BasicBlock b : phiBlocks.get(v)) {
                if (!lVersionMap.containsKey(b.getName())) {
                    continue;
                }
                PhiPrimitive phiNode = new PhiPrimitive();
                LinkedHashMap<String, Primitive> phiMap = new LinkedHashMap<>();
                for (BasicBlock par : b.getParents()) {
                    VarPrimitive var = new VarPrimitive(v);
                    Integer val = lVersionMap.get(par.getName()).get(v);
                    var.setVersion(val);
                    phiMap.put(par.getName(), var);
                }
                phiNode.setVarMap(phiMap);
                VarPrimitive var = new VarPrimitive(v);
                Integer val = rVersionMap.get(b.getName()).get(v);
                var.setVersion(val);
                IREqual ir = new IREqual(var, phiNode);
                b.insertStart(ir);
            }
        }

        for (BasicBlock b : blocks) {
            if (b.getControlStmt() == null || !currentBlocks.contains(b.getName())) {
                continue;
            }
            for (String v : varCount.keySet()) {
                int r = rVersionMap.get(b.getName()).get(v);
                int l = lVersionMap.get(b.getName()).get(v);
                VersionSetter ver = new VersionSetter(v, r, l);
                ver.visit(b);
            }
        }
    }

    @Override
    public void visit(BasicBlock node) {
        for (IRStmt ir : node.getStatements()) {
            ir.accept(this);
        }
        if (node.isHead()) {
            for (String var : node.getParams()) {
                if (!blocksByAssignedVar.containsKey(var)) {
                    blocksByAssignedVar.put(var, new HashSet<>());
                }
                blocksByAssignedVar.get(var).add(node);
            }
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
        String blockName = currentBlock.getName();
        String varName = node.getName();

        if (isEqualStmt) {
            if (!blocksByAssignedVar.containsKey(varName)) {
                blocksByAssignedVar.put(varName, new HashSet<>());
            }
            blocksByAssignedVar.get(varName).add(currentBlock);

            if (!rVersionMap.get(blockName).containsKey(varName)) {
                rVersionMap.get(blockName).put(varName, varCount.get(varName));
            }
            varCount.put(varName, varCount.get(varName) + 1);
            lVersionMap.get(blockName).put(varName, varCount.get(varName));
        }
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
