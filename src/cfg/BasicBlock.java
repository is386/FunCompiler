package cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import cfg.stmt.ControlStmt;
import cfg.stmt.IRStmt;
import visitor.CFGVisitor;

public class BasicBlock {
    private final String name;
    private Stack<IRStmt> statements = new Stack<>();
    private ControlStmt control = null;
    private ArrayList<BasicBlock> parents = new ArrayList<>();
    private ArrayList<BasicBlock> children = new ArrayList<>();
    private HashSet<BasicBlock> dominators = new HashSet<>();
    private ArrayList<String> params = new ArrayList<>();
    private boolean isHead = false;

    public BasicBlock(String name) {
        this.name = name;
    }

    public void push(IRStmt ir) {
        statements.add(ir);
    }

    public void insertStart(IRStmt ir) {
        statements.insertElementAt(ir, 0);
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

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public void setAsHead() {
        isHead = true;
    }

    public boolean unreachable() {
        return parents.size() + children.size() == 0 && !isHead;
    }

    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    public HashSet<BasicBlock> getDominators() {
        return dominators;
    }

    public void setDominators(HashSet<BasicBlock> d) {
        this.dominators = new HashSet<>(d);
    }

    public void addDominator(BasicBlock d) {
        this.dominators.add(d);
    }

    public String toString() {
        String s = name;
        if (!params.isEmpty()) {
            s += "(";
            for (String p : params) {
                s += "%" + p + "0, ";
            }
            s = s.substring(0, s.length() - 2) + ")";
        }
        s += ":\n";
        for (IRStmt ir : statements) {
            s += "    " + ir + "\n";
        }
        if (control != null) {
            s += control + "\n";
        }
        return s;
    }

}