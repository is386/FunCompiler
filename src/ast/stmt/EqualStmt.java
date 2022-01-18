package ast.stmt;

import org.json.JSONObject;

import ast.ASTVistor;
import ast.expr.ASTExpr;

public class EqualStmt extends ASTStmt {
    private final String var;
    private final ASTExpr expr;

    public EqualStmt(String var, ASTExpr expr) {
        this.var = var.isEmpty() ? "_" : var;
        this.expr = expr;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("var", var)
                .put("expr", new JSONObject(expr.toString()))
                .toString();
    }

    @Override
    public void accept(ASTVistor vistor) {
        // TODO Auto-generated method stub

    }

}
