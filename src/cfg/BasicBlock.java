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
    // private ArrayList<VarPrimitive> vars = new ArrayList<>();
    private boolean isHead = false;

    public BasicBlock(String name) {
        this.name = name;
    }

    public void push(IRStmt ir) {
        // if (ir instanceof IREqual) {
        // IREqual eq = (IREqual) ir;
        // if (eq.getVar() instanceof VarPrimitive && !(eq.getVar() instanceof
        // TempPrimitive)) {
        // vars.add((VarPrimitive) eq.getVar());
        // }
        // }
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

    // public ArrayList<VarPrimitive> getVars() {
    // return vars;
    // }

    public Stack<IRStmt> getStatements() {
        return statements;
    }

    public void setControlStmt(ControlStmt c) {
        // if (c instanceof ControlCond) {
        // ControlCond cc = (ControlCond) c;
        // if (cc.getCond() instanceof VarPrimitive && !(cc.getCond() instanceof
        // TempPrimitive)) {
        // vars.add((VarPrimitive) cc.getCond());
        // }
        // } else if (c instanceof ControlReturn) {
        // ControlReturn cc = (ControlReturn) c;
        // if (cc.getValue() instanceof VarPrimitive && !(cc.getValue() instanceof
        // TempPrimitive)) {
        // vars.add((VarPrimitive) cc.getValue());
        // }
        // }
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