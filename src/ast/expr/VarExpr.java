package ast.expr;

import org.json.JSONObject;

import ir.CFGVisitor;

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

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
