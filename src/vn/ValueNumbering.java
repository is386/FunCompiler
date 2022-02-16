package vn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.*;

public class ValueNumbering {

    private static HashMap<Primitive, Primitive> VN = new HashMap<>();
    private static HashMap<Primitive, Primitive> hashTable = new HashMap<>();

    public static void doVN(CFG cfg) {
        for (int i = 0; i < cfg.getNumFuncs(); i++) {
            VN = new HashMap<>();
            hashTable = new HashMap<>();
            HashSet<String> blockNames = cfg.getFuncBlocks(i);

            for (BasicBlock b : cfg.getBlocks()) {
                if (blockNames.contains(b.getName()) && !b.getName().startsWith("l")) {
                    dvnt(b);
                }
            }
        }
    }

    public static void dvnt(BasicBlock b) {
        if (b.isFail()) {
            return;
        }

        for (IREqual phi : b.getPhiNodes()) {
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
        ArrayList<IRStmt> toRemove = new ArrayList<>();

        for (IRStmt stmt : b.getStatements()) {
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

            if (y instanceof IntPrimitive) {
                VN.put(y, y);
            }
            if (z instanceof IntPrimitive) {
                VN.put(z, z);
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

        for (IRStmt ir : toRemove) {
            b.removeStatement(ir);
        }

        ControlStmt c = b.getControlStmt();
        if (c instanceof ControlCond) {
            ControlCond cc = (ControlCond) c;
            cc.setCond(VN.get(cc.getCond()));
        }

        // Traverse dominator tree
        for (BasicBlock d : b.getdomChildren()) {
            dvnt(d);
        }
    }
}
