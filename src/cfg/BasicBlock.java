package cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import cfg.stmt.ControlStmt;
import cfg.stmt.IREqual;
import cfg.stmt.IRStmt;
import visitor.CFGVisitor;

public class BasicBlock {
    private final String name;
    private final Integer num;
    private Stack<IRStmt> statements = new Stack<>();
    private ArrayList<IREqual> phiNodes = new ArrayList<>();
    private ControlStmt control = null;
    private ArrayList<BasicBlock> parents = new ArrayList<>();
    private ArrayList<BasicBlock> children = new ArrayList<>();
    private HashSet<BasicBlock> dominators = new HashSet<>();
    private HashSet<BasicBlock> domChildren = new HashSet<>();
    private BasicBlock iDom = null;
    private HashSet<BasicBlock> df = new HashSet<>();
    private ArrayList<String> params = new ArrayList<>();
    private boolean isHead = false;
    private boolean isFail = false;

    public BasicBlock(String name, Integer num) {
        this.name = name;
        this.num = num;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setFail() {
        isFail = true;
    }

    public boolean isFail() {
        return isFail;
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

    public ArrayList<String> getParams() {
        return params;
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

    public BasicBlock getiDom() {
        return iDom;
    }

    public void setiDom(BasicBlock iDom) {
        this.iDom = iDom;
    }

    public HashSet<BasicBlock> getDF() {
        return df;
    }

    public void setDF(HashSet<BasicBlock> df) {
        this.df = new HashSet<>(df);
    }

    public HashSet<BasicBlock> getdomChildren() {
        return domChildren;
    }

    public void addDomChild(BasicBlock d) {
        this.domChildren.add(d);
    }

    public void removeDomChild(BasicBlock d) {
        this.domChildren.remove(d);
    }

    public void printDominators() {
        System.out.print("\n" + name + ": ");
        for (BasicBlock b : dominators) {
            if (b == iDom) {
                System.out.print("(iDom)");
            }
            System.out.print(b.getNum() + ", ");
        }
        System.out.print("\n");
        for (BasicBlock b : df) {
            System.out.print(b.getNum() + ", ");
        }
        System.out.print("\n");
    }

    public Integer getNum() {
        return num;
    }

    public ArrayList<IREqual> getPhiNodes() {
        return phiNodes;
    }

    public void addPhiNode(IREqual p) {
        phiNodes.add(p);
    }

    public void removePhiNode(IREqual p) {
        phiNodes.remove(p);
    }

    public void removeStatement(IRStmt ir) {
        statements.remove(ir);
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
        for (IREqual ir : phiNodes) {
            s += " " + ir + "\n";
        }
        for (IRStmt ir : statements) {
            s += "    " + ir + "\n";
        }
        if (control != null) {
            s += control + "\n";
        }
        return s;
    }

}