package ir;

import java.util.Stack;

import ir.stmt.ControlStmt;
import ir.stmt.IRStmt;

public class BasicBlock {
    private final String name;
    private Stack<IRStmt> statements = new Stack<>();
    private ControlStmt control = null;

    public BasicBlock(String name) {
        this.name = name;
    }

    public void push(IRStmt p) {
        statements.add(p);
    }

    public IRStmt pop() {
        return statements.pop();
    }

    public IRStmt peek() {
        return statements.peek();
    }

    public void setControlStmt(ControlStmt c) {
        control = c;
    }

    public ControlStmt getControlStmt() {
        return control;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String s = name + ":\n";
        for (IRStmt ir : statements) {
            s += "    " + ir + "\n";
        }
        if (control != null) {
            s += control + "\n";
        }
        return s;
    }

}