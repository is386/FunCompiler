package ir;

import java.util.ArrayList;

public class BasicBlock {
    private final String name;
    private ArrayList<IRStmt> statements = new ArrayList<>();
    private int varCounter;

    public BasicBlock(String name, int varCounter) {
        this.name = name;
        this.varCounter = varCounter;
    }

    public void addStatement(IRStmt p) {
        statements.add(p);
    }

    public int getVarCounter() {
        return varCounter;
    }

    public void incrVarCounter() {
        varCounter++;
    }
}