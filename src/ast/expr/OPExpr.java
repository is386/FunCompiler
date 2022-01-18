package ast.expr;

import ir.CFGVisitor;

public class OPExpr extends ASTExpr {

    private final Character op;

    public OPExpr(Character op) {
        this.op = op;
    }

    public String toString() {
        return op.toString();
    }

    @Override
    public void accept(CFGVisitor visitor) {
    }
}
