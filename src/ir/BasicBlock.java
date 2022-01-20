package ir;

import java.util.Stack;

import ir.stmt.IRStmt;

public class BasicBlock {
    private final String name;
    private Stack<IRStmt> statements = new Stack<>();

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

    public String toString() {
        String s = name + ":\n";
        for (IRStmt ir : statements) {
            s += "    " + ir + "\n";
        }
        return s;
    }

}