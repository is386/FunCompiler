package cfg.stmt;

import java.util.ArrayList;

import visitor.CFGVisitor;

public class ControlJump extends ControlStmt {
    private final String name;

    public ControlJump(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "    jump " + name;
    }

    @Override
    public ArrayList<String> getBranchNames() {
        ArrayList<String> a = new ArrayList<>();
        a.add(name);
        return a;
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
