package cfg.primitives;

import cfg.Type;
import ssa.SSAVisitor;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
        setType(Type.THIS);
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
