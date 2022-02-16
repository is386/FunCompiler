package vn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cfg.BasicBlock;
import cfg.CFG;
import cfg.primitives.*;
import cfg.stmt.IREqual;
import cfg.stmt.IRStmt;

public class ValueNumbering {

    private static HashMap<Primitive, Primitive> VN = new HashMap<>();
    private static HashMap<Primitive, Primitive> hashTable = new HashMap<>();

    public static void doVN(CFG cfg) {
        for (int i = 0; i < cfg.getNumFuncs(); i++) {
            ArrayList<BasicBlock> blocks = new ArrayList<>();
            HashSet<String> blockNames = cfg.getFuncBlocks(i);

            for (BasicBlock b : cfg.getBlocks()) {
                if (blockNames.contains(b.getName())) {
                    blocks.add(b);
                }
            }

            dvnt(blocks.get(0));
        }
        // System.out.println(VN);
        // System.out.println(hashTable);
    }

    public static void dvnt(BasicBlock b) {
        for (IREqual phi : b.getPhiNodes()) {
            Primitive p = phi.getPrimitive();
            Primitive n = phi.getVar();

            if (hashTable.containsKey(p)) {
                Primitive pVN = hashTable.get(p);
                VN.put(n, pVN);
            } else {
                VN.put(n, n);
                hashTable.put(p, n);
            }
        }

        ArrayList<IRStmt> toRemove = new ArrayList<>();

        for (IRStmt stmt : b.getStatements()) {
            if (!(stmt instanceof IREqual)) {
                continue;
            }

            IREqual a = (IREqual) stmt;
            VarPrimitive x = (VarPrimitive) a.getVar();

            if (a.getPrimitive() instanceof IntPrimitive) {
                VN.put(x, a.getPrimitive());
            }
            if (!(a.getPrimitive() instanceof ArithPrimitive)) {
                continue;
            }

            ArithPrimitive oldExpr = (ArithPrimitive) a.getPrimitive();
            ArithPrimitive newExpr = new ArithPrimitive(oldExpr.getOp());

            Primitive y = oldExpr.getOperand1();
            Primitive z = oldExpr.getOperand2();
            if (y instanceof IntPrimitive) {
                VN.put(y, y);
            }
            if (z instanceof IntPrimitive) {
                VN.put(z, z);
            }
            newExpr.setOperand1(VN.get(y));
            newExpr.setOperand2(VN.get(z));
            oldExpr.setOperand1(VN.get(y));
            oldExpr.setOperand2(VN.get(z));

            if (hashTable.containsKey(newExpr)) {
                Primitive v = hashTable.get(newExpr);
                VN.put(x, v);
                toRemove.add(a);
            } else {
                VN.put(x, x);
                hashTable.put(newExpr, x);
            }

            for (BasicBlock c : b.getDominates()) {
                dvnt(c);
            }
        }

        for (IRStmt ir : toRemove) {
            // b.removeStatement(ir);
        }
    }
}
