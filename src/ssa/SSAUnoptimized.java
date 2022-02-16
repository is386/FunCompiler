package ssa;

import java.util.*;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class SSAUnoptimized implements CFGVisitor {

    private CFG cfg;
    private BasicBlock currentBlock;
    private boolean isEqualStmt = false;
    private LinkedHashMap<String, HashMap<String, Integer>> lVersionMap = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<String, Integer>> rVersionMap = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> varCount = new LinkedHashMap<>();
    private HashSet<BasicBlock> phiBlocks = new HashSet<>();
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
        phiBlocks = new HashSet<>();
        currentBlocks = new HashSet<>();
        isEqualStmt = false;
        currentBlock = null;
    }

    public void setVersions(ArrayList<BasicBlock> blocks) {
        for (BasicBlock b : blocks) {
            if (b.getControlStmt() == null || !currentBlocks.contains(b.getName())) {
                continue;
            }
            if (b.getParents().size() >= 2) {
                phiBlocks.add(b);
            }
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

        if (phiBlocks.isEmpty()) {
            return;
        }

        for (BasicBlock b : phiBlocks) {
            for (String v : varCount.keySet()) {
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
                b.addPhiNode(ir);
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
