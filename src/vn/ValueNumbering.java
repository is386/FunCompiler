package vn;

import java.util.*;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.CFGVisitor;

public class ValueNumbering implements CFGVisitor {

    private HashMap<Primitive, Primitive> VN = new HashMap<>();
    private Stack<HashMap<Primitive, Primitive>> scope = new Stack<>();
    private boolean replaceCond = false;
    private String jumpTo;

    public void visit(CFG cfg) {
        for (int i = 0; i < cfg.getNumFuncs(); i++) {
            VN = new HashMap<>();
            scope = new Stack<>();
            scope.add(new HashMap<>());
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

        // Get the hash table for the current scope
        HashMap<Primitive, Primitive> hashTable = new HashMap<>(scope.peek());

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

            if (a.getPrimitive() instanceof AllocPrimitive) {
                VN.put(x, a.getPrimitive());
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
            if (VN.get(y) == null || (VN.get(z) == null) || !notAlloc(VN.get(y)) || !notAlloc(VN.get(z))) {
                continue;
            }

            Primitive a2;

            // Store operands VN[y] and VN[z] in new expression and overwrite
            // old expression
            newExpr.setOperand1(VN.get(y));
            newExpr.setOperand2(VN.get(z));
            oldExpr.setOperand1(VN.get(y));
            oldExpr.setOperand2(VN.get(z));

            // Check identities and replace if necessary
            a2 = newExpr;
            Primitive identity = minusIdentity(newExpr);
            if (!identity.equals(newExpr)) {
                a.setPrimitive(identity);
                a2 = identity;
            }
            identity = andIdentity(newExpr);
            if (!identity.equals(newExpr)) {
                a.setPrimitive(identity);
                a2 = identity;
            }
            identity = addIdentity(newExpr);
            if (!identity.equals(newExpr)) {
                a.setPrimitive(identity);
                a2 = identity;
            }

            // If the identity is replaced, we can put it into VN and continue
            if (!(a.getPrimitive() instanceof ArithPrimitive)) {
                VN.put(x, a.getPrimitive());
                toRemove.add(a);
                continue;
            }

            // If the new expression is found in the hashtable, replace it's
            // left side with the value number of the expression and remove a
            if (hashTable.containsKey(a2)) {
                Primitive v = hashTable.get(a2);
                VN.put(x, v);
                toRemove.add(a);
            } else {
                VN.put(x, x);
                hashTable.put(a2, x);
            }
        }

        // Add the hash table for the current scope to the global list
        scope.add(hashTable);

        // Remove statements from block if necessary
        for (IREqual ir : toRemove) {
            node.removeStatement(ir);
        }

        // Replace conditional if needed
        node.getControlStmt().accept(this);

        // If the condition is 1, then we can replace it with a jump
        if (replaceCond) {
            node.setControlStmt(new ControlJump(jumpTo));
        }
        replaceCond = false;

        // Replace values in successor phi nodes
        for (BasicBlock s : node.getChildren()) {
            for (IREqual phi : s.getPhiNodes()) {
                phi.getPrimitive().accept(this);
            }
        }

        // Traverse dominator tree
        for (BasicBlock d : node.getdomChildren()) {
            d.accept(this);
        }

        // Remove the hash table currently in scope
        scope.pop();
    }

    private Primitive minusIdentity(ArithPrimitive arith) {
        Primitive x = arith.getOperand1();
        Primitive y = arith.getOperand2();
        String op = arith.getOp();

        if (op.equals("-") && x.equals(y)) {
            return new IntPrimitive(0);
        }

        return arith;
    }

    private Primitive andIdentity(ArithPrimitive arith) {
        Primitive x = arith.getOperand1();
        Primitive y = arith.getOperand2();
        String op = arith.getOp();

        if (op.equals("&") && x.equals(y)) {
            return x;
        }

        return arith;
    }

    private Primitive addIdentity(ArithPrimitive arith) {
        Primitive x = arith.getOperand1();
        Primitive y = arith.getOperand2();
        Primitive zero = new IntPrimitive(0);
        String op = arith.getOp();

        if (op.equals("+") && (x.equals(zero))) {
            return y;
        } else if (op.equals("+") && (y.equals(zero))) {
            return x;
        }

        return arith;
    }

    private boolean notAlloc(Primitive p) {
        return !(p instanceof AllocPrimitive);
    }

    @Override
    public void visit(CallPrimitive node) {
        if (VN.containsKey(node.getCaller()) && notAlloc(VN.get(node.getCaller()))) {
            node.setCaller(VN.get(node.getCaller()));
        }
        if (VN.containsKey(node.getCodeAddress()) && notAlloc(VN.get(node.getCodeAddress()))) {
            node.setCodeAddress(VN.get(node.getCodeAddress()));
        }
        for (Primitive p : node.getArgs()) {
            int i = node.getArgs().indexOf(p);
            if (VN.containsKey(p) && notAlloc(VN.get(p))) {
                node.getArgs().set(i, VN.get(p));
            }
        }
    }

    @Override
    public void visit(GetEltPrimitive node) {
        if (VN.containsKey(node.getPrimitive()) && notAlloc(VN.get(node.getPrimitive()))) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
        if (VN.containsKey(node.getOffset()) && notAlloc(VN.get(node.getOffset()))) {
            node.setOffset(VN.get(node.getOffset()));
        }
    }

    @Override
    public void visit(LoadPrimitive node) {
        if (VN.containsKey(node.getPrimitive()) && notAlloc(VN.get(node.getPrimitive()))) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
    }

    @Override
    public void visit(PhiPrimitive node) {
        for (String l : node.getVarMap().keySet()) {
            Primitive p = node.getVarMap().get(l);
            if (VN.containsKey(p) && notAlloc(VN.get(p))) {
                node.getVarMap().put(l, VN.get(p));
            }
        }
    }

    @Override
    public void visit(PrintPrimitive node) {
        if (VN.containsKey(node.getPrimitive()) && notAlloc(VN.get(node.getPrimitive()))) {
            node.setPrimitive(VN.get(node.getPrimitive()));
        }
    }

    @Override
    public void visit(SetEltPrimitive node) {
        if (VN.containsKey(node.getLocation()) && notAlloc(VN.get(node.getLocation()))) {
            node.setLocation(VN.get(node.getLocation()));
        }
        if (VN.containsKey(node.getVar()) && notAlloc(VN.get(node.getVar()))) {
            node.setVar(VN.get(node.getVar()));
        }
        if (VN.containsKey(node.getValue()) && notAlloc(VN.get(node.getValue()))) {
            node.setValue(VN.get(node.getValue()));
        }
    }

    @Override
    public void visit(StorePrimitive node) {
        if (VN.containsKey(node.getValue()) && notAlloc(VN.get(node.getValue()))) {
            node.setValue(VN.get(node.getValue()));
        }
    }

    @Override
    public void visit(ControlCond node) {
        if (VN.containsKey(node.getCond()) && !(VN.get(node.getCond()) instanceof AllocPrimitive)) {
            node.setCond(VN.get(node.getCond()));
        }
        Primitive one = new IntPrimitive(1);
        if (node.getCond().equals(one)) {
            jumpTo = node.getIf();
            replaceCond = true;
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
