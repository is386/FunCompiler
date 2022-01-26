package ast.expr;

import cfg.CFGBuilder;

public class OPExpr extends ASTExpr {

    private final Character op;

    public OPExpr(Character op) {
        this.op = op;
    }

    public String toString() {
        return op.toString();
    }

    @Override
    public void accept(CFGBuilder visitor) {
    }
}
