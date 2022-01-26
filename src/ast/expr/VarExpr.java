package ast.expr;

import org.json.JSONObject;

import cfg.CFGBuilder;

public class VarExpr extends ASTExpr {
    private final String name;

    public VarExpr(String name) {
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

    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }
}
