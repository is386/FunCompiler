package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import visitor.ASTVisitor;

public class AssignStmt extends ASTStmt {
    private final String var;
    private final ASTExpr expr;

    public AssignStmt(String var, ASTExpr expr) {
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

    public String getVar() {
        return var;
    }

    public ASTExpr getExpr() {
        return expr;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
