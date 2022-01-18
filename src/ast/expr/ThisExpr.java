package ast.expr;

import org.json.JSONObject;

import ast.ASTVistor;

public class ThisExpr extends ASTExpr {

    public ThisExpr() {
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .toString();
    }

    @Override
    public void accept(ASTVistor vistor) {
        // TODO Auto-generated method stub

    }

}
