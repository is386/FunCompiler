package cfg;

import java.util.ArrayList;
import java.util.Stack;

import cfg.stmt.ControlStmt;
import cfg.stmt.IRStmt;
import ssa.SSAVisitor;

public class BasicBlock {
    private final String name;
    private Stack<IRStmt> statements = new Stack<>();
    private ControlStmt control = null;
    private ArrayList<BasicBlock> parents = new ArrayList<>();
    private ArrayList<BasicBlock> children = new ArrayList<>();
    private boolean isHead = false;

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

    public Stack<IRStmt> getStatements() {
        return statements;
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

    public ArrayList<BasicBlock> getChildren() {
        return children;
    }

    public void addChild(BasicBlock child) {
        children.add(child);
    }

    public ArrayList<BasicBlock> getParents() {
        return parents;
    }

    public void addParent(BasicBlock parent) {
        parents.add(parent);
    }

    public void setAsHead() {
        isHead = true;
    }

    public boolean unreachable() {
        return parents.size() + children.size() == 0 && !isHead;
    }

    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
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