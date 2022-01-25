package cfg.primitives;

import ssa.SSAVisitor;

public class TempPrimitive extends VarPrimitive {
    public TempPrimitive(String name) {
        super(name);
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
