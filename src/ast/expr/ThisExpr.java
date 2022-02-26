package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class ThisExpr extends ASTExpr {

    public ThisExpr() {
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
