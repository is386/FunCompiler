package ir;

import java.util.ArrayList;

import ir.stmt.ControlStmt;
import ir.stmt.IRStmt;

public class BasicBlock {
    private final String name;
    private ArrayList<IRStmt> primitives = new ArrayList<>();
    private ControlStmt control;
    private int varCounter;

    public BasicBlock(String name, int varCounter) {
        this.name = name;
        this.varCounter = varCounter;
    }

    public void addPrimitive(IRStmt p) {
        primitives.add(p);
    }

    public void setControl(ControlStmt c) {
        control = c;
    }

    public int getVarCounter() {
        return varCounter;
    }

    public void incrVarCounter() {
        varCounter++;
    }
}
