package ast.expr;

import cfg.CFGTransformer;

public class OPExpr extends ASTExpr {

    private final Character op;

    public OPExpr(Character op) {
        this.op = op;
    }

    public String toString() {
        return op.toString();
    }

    @Override
    public void accept(CFGTransformer visitor) {
    }
}
