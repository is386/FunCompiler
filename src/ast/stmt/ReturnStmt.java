package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import visitor.ASTVisitor;

public class ReturnStmt extends ASTStmt {
    private final ASTExpr expr;

    public ReturnStmt(ASTExpr expr) {
        this.expr = expr;
    }

    public ASTExpr getExpr() {
        return expr;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("expr", new JSONObject(expr.toString()))
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
