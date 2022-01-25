package cfg.stmt;

import java.util.ArrayList;

import cfg.primitives.Primitive;
import ssa.SSAVisitor;

public class ControlReturn extends ControlStmt {
    private final Primitive value;

    public ControlReturn(Primitive value) {
        this.value = value;
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
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
