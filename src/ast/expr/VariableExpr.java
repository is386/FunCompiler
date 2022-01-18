package ast.expr;

import org.json.JSONObject;

import ast.ASTVistor;

public class VariableExpr extends ASTExpr {
    private final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
