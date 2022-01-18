package ast.expr;

import ir.BasicBlock;
import ir.primitives.Primitive;

public abstract class ASTExpr {

    public abstract String toString();

    public abstract Primitive toPrimitive(BasicBlock block);

    public String getName() {
        return "";
    }
}