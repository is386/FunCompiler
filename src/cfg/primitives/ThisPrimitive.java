package cfg.primitives;

import visitor.CFGVisitor;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "%this";
    }
}
