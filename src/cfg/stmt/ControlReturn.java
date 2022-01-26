package cfg.stmt;

import java.util.ArrayList;

import cfg.primitives.Primitive;
import visitor.CFGVisitor;

public class ControlReturn extends ControlStmt {
    private final Primitive value;

    public ControlReturn(Primitive value) {
        this.value = value;
    }

    public Primitive getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "    ret " + value;
    }

    @Override
    public ArrayList<String> getBranchNames() {
        return new ArrayList<>();
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
