package ast.stmt;

import ast.expr.ASTExpr;

public class PrintStmt extends ASTStmt {
    private final ASTExpr expr;

    public PrintStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public ASTExpr getExpr() {
        return expr;
    }

    public String toString() {
        return "print(" + expr.toString() + ")";
    }
}
