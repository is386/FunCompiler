public class ReturnStmt extends ASTStmt {
    private final ASTExpr expr;

    public ReturnStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public ASTExpr getExpr() {
        return expr;
    }

    public String toString() {
        return "return " + expr.toString();
    }
}
