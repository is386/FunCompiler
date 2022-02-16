package vn;

import java.util.*;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class ValueNumbering implements CFGVisitor {

    private HashMap<Primitive, Primitive> VN = new HashMap<>();
    private Stack<HashMap<Primitive, Primitive>> hashTables = new Stack<>();

    public void visit(CFG cfg) {
        for (int i = 0; i < cfg.getNumFuncs(); i++) {
            VN = new HashMap<>();
            hashTables = new Stack<>();
            hashTables.add(new HashMap<>());
            HashSet<String> blockNames = cfg.getFuncBlocks(i);

            for (BasicBlock b : cfg.getBlocks()) {
                if (blockNames.contains(b.getName()) && !b.getName().startsWith("l")) {
                    b.accept(this);
                }
            }
        }
    }

    @Override
    public void visit(BasicBlock node) {
        if (node.isFail()) {
            return;
        }
        HashMap<Primitive, Primitive> hashTable = new HashMap<>(hashTables.peek());

        for (IREqual phi : node.getPhiNodes()) {
            Primitive p = phi.getPrimitive();
            Primitive n = phi.getVar();

            // If p is meaningless or redundant
            if (hashTable.containsKey(p)) {
                Primitive pVN = hashTable.get(p);
                VN.put(n, pVN);
            } else {
                VN.put(n, n);
                hashTable.put(p, n);
            }
        }

        // Statements to remove later
        ArrayList<IREqual> toRemove = new ArrayList<>();

        for (IRStmt stmt : node.getStatements()) {
            // If its not an equal statement, skip it
            if (!(stmt instanceof IREqual)) {
                continue;
            }

            // Type cast statement to equal statement, and get var
            IREqual a = (IREqual) stmt;
            Primitive x = a.getVar();

            // If the right side is an integer, put it in the VN
            if (a.getPrimitive() instanceof IntPrimitive) {
                VN.put(x, a.getPrimitive());
            }

            if (a.getPrimitive() instanceof VarPrimitive) {
                a.setPrimitive(VN.get(x));
            }

            // If the right side is not an arithmetic operation, skip this statement
            if (!(a.getPrimitive() instanceof ArithPrimitive)) {
                a.getPrimitive().accept(this);
                continue;
            }

            // Type cast right side to arithmetic operation
            ArithPrimitive oldExpr = (ArithPrimitive) a.getPrimitive();

            // New expression will use the same operation
            ArithPrimitive newExpr = new ArithPrimitive(oldExpr.getOp());

            // Get the operands of the old expression and if they are integers,
            // put them in the VN equal to themselves
            Primitive y = oldExpr.getOperand1();
            Primitive z = oldExpr.getOperand2();

            if (y instanceof IntPrimitive || y instanceof ThisPrimitive) {
                VN.put(y, y);
            }
            if (z instanceof IntPrimitive || z instanceof ThisPrimitive) {
                VN.put(z, z);
            }
            if (VN.get(y) == null || (VN.get(z) == null)) {
                continue;
            }

            // Store operands VN[y] and VN[z] in new expression and overwrite
            // old expression
            newExpr.setOperand1(VN.get(y));
            newExpr.setOperand2(VN.get(z));
            oldExpr.setOperand1(VN.get(y));
            oldExpr.setOperand2(VN.get(z));

            // If the new expression is found in the hashtable, replace it's
            // left side with the value number of the expression and remove a
            if (hashTable.containsKey(newExpr)) {
                Primitive v = hashTable.get(newExpr);
                VN.put(x, v);
                toRemove.add(a);
            } else {
                VN.put(x, x);
                hashTable.put(newExpr, x);
            }
        }

        hashTables.add(hashTable);

        for (IREqual ir : toRemove) {
            node.removeStatement(ir);
        }

        ControlStmt c = node.getControlStmt();
        if (c instanceof ControlCond) {
            ControlCond cc = (ControlCond) c;
            if (VN.containsKey(cc.getCond())) {
                cc.setCond(VN.get(cc.getCond()));
            }
        }

        for (BasicBlock s : node.getChildren()) {
            for (IREqual phi : s.getPhiNodes()) {
                phi.getPrimitive().accept(this);
            }
        }

        // Traverse dominator tree
        for (BasicBlock d : node.getdomChildren()) {
            d.accept(this);
        }
        hashTables.pop();
    }

    @Override
    public void visit(CallPrimitive node) {
        if (VN.containsKey(node.getCaller())) {
            node.setCaller(VN.get(node.getCaller()));
        }
        if (VN.containsKey(node.getCodeAddress())) {
            node.setCodeAddress(VN.get(node.getCodeAddress()));
        }
        for (Primitive p : node.getArgs()) {
            int i = node.getArgs().indexOf(p);
            if (VN.containsKey(p)) {
                node.getArgs().set(i, VN.get(p));
            }
        }
    }

    @Override
    public void visit(GetEltPrimitive node) {
        if (VN.containsKey(node.getPrimitive())) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
        if (VN.containsKey(node.getOffset())) {
            node.setOffset(VN.get(node.getOffset()));
        }
    }

    @Override
    public void visit(LoadPrimitive node) {
        if (VN.containsKey(node.getPrimitive())) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
    }

    @Override
    public void visit(PhiPrimitive node) {
        for (String l : node.getVarMap().keySet()) {
            Primitive p = node.getVarMap().get(l);
            if (VN.containsKey(p)) {
                node.getVarMap().put(l, VN.get(p));
            }
        }
    }

    @Override
    public void visit(PrintPrimitive node) {
        if (VN.containsKey(node.getPrimitive())) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
    }

    @Override
    public void visit(SetEltPrimitive node) {
        if (VN.containsKey(node.getLocation())) {
            node.setLocation(VN.get(node.getLocation()));
        }
        if (VN.containsKey(node.getVar())) {
            node.setVar(VN.get(node.getVar()));
        }
        if (VN.containsKey(node.getValue())) {
            node.setValue(VN.get(node.getValue()));
        }
    }

    @Override
    public void visit(StorePrimitive node) {
        // if (VN.containsKey(node.getLocation())) {
        // node.setLocation(VN.get(node.getLocation()));
        // }
        if (VN.containsKey(node.getValue())) {
            node.setValue(VN.get(node.getValue()));
        }
    }

    @Override
    public void visit(IREqual node) {
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
    public void visit(ControlJump node) {
    }

    @Override
    public void visit(ControlReturn node) {
    }

    @Override
    public void visit(AllocPrimitive node) {
    }

    @Override
    public void visit(ArithPrimitive node) {
    }

    @Override
    public void visit(GlobalPrimitive node) {
    }

    @Override
    public void visit(IntPrimitive node) {
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
