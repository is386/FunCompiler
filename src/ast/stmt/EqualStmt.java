package ast.stmt;

import ast.expr.ASTExpr;

public class EqualStmt extends ASTStmt {
    private final String var;
    private final ASTExpr expr;

    public EqualStmt(String var, ASTExpr expr) {
        this.var = var.isEmpty() ? "_" : var;
        this.expr = expr;
    }

    public String toString() {
        return var + " = " + expr;
    }

}
