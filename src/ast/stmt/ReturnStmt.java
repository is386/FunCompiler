package ast.stmt;

import ast.expr.ASTExpr;

public class ReturnStmt extends ASTStmt {
    private final ASTExpr expr;

    public ReturnStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public String toString() {
        return "return " + expr;
    }
}
