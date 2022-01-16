package ast.expr;

public class ArithExpr extends ASTExpr {
    private final ASTExpr op;
    private final ASTExpr expr1;
    private final ASTExpr expr2;

    public ArithExpr(ASTExpr op, ASTExpr expr1, ASTExpr expr2) {
        this.op = op;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public ASTExpr getOP() {
        return op;
    }

    public ASTExpr getExpr1() {
        return expr1;
    }

    public ASTExpr getExpr2() {
        return expr2;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", expr1, op, expr2);
    }
}
