package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import ir.CFGVisitor;

public class ReturnStmt extends ASTStmt {
    private final ASTExpr expr;

    public ReturnStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("expr", new JSONObject(expr.toString()))
                .toString();
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
