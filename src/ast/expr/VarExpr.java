package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class VarExpr extends ASTExpr {
    protected final String name;

    public VarExpr(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
