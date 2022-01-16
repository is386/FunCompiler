package ast.stmt;

import ast.expr.ASTExpr;

public class EqualStmt extends ASTStmt {
    private final String var;
    private final ASTExpr expr;

    public EqualStmt(String var, ASTExpr expr) {
        this.var = var;
        this.expr = expr;
    }

    public String getVar() {
        return var;
    }

    public ASTExpr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return var + " = " + expr.toString();
    }

}
