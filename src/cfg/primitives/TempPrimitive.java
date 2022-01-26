package cfg.primitives;

import visitor.CFGVisitor;

public class TempPrimitive extends VarPrimitive {
    public TempPrimitive(String name) {
        super(name);
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
