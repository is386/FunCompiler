package ast.expr;

import org.json.JSONObject;

import cfg.CFGBuilder;

public class NullExpr extends ASTExpr {
    private final String type;

    public NullExpr(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("type", type)
                .toString();
    }

    @Override
    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }

}
