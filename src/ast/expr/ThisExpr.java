package ast.expr;

import org.json.JSONObject;

public class ThisExpr extends ASTExpr {

    public ThisExpr() {
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .toString();
    }

}
