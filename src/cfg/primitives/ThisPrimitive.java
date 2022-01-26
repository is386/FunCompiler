package cfg.primitives;

import cfg.Type;
import visitor.CFGVisitor;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
        setType(Type.THIS);
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
