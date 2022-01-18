package ast.expr;

import org.json.JSONObject;

import ast.ASTVistor;

public class ClassExpr extends ASTExpr {
    private final String name;

    public ClassExpr(String name) {
        this.name = name;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .toString();
    }

    @Override
    public void accept(ASTVistor vistor) {
        // TODO Auto-generated method stub

    }
}
