package ast.expr;

import ir.BasicBlock;
import ir.primitives.Primitive;

public class OPExpr extends ASTExpr {

    private final Character op;

    public OPExpr(Character op) {
        this.op = op;
    }

    public String toString() {
        return op.toString();
    }

    @Override
    public Primitive toPrimitive(BasicBlock block) {
        // TODO Auto-generated method stub
        return null;
    }
}
