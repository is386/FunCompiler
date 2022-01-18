package ast.stmt;

import org.json.JSONObject;

import ast.ASTVistor;
import ast.expr.ASTExpr;

public class PrintStmt extends ASTStmt {
    private final ASTExpr expr;

    public PrintStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("expr", new JSONObject(expr.toString()))
                .toString();
    }

    @Override
    public void accept(ASTVistor vistor) {
        // TODO Auto-generated method stub

    }
}
