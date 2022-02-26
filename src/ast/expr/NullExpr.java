package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class NullExpr extends ASTExpr {
    private final String type;

    public NullExpr(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("type", type)
                .toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
