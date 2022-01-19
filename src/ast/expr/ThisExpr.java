package ast.expr;

import org.json.JSONObject;

import ir.CFGVisitor;

public class ThisExpr extends ASTExpr {

    public ThisExpr() {
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .toString();
    }

    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
