package ast.expr;

import org.json.JSONObject;

import cfg.CFGVisitor;

public class ClassExpr extends ASTExpr {
    private final String name;

    public ClassExpr(String name) {
        this.name = name;
    }

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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
