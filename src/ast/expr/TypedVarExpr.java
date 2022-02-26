package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class TypedVarExpr extends VarExpr {
    private final String type;

    public TypedVarExpr(String name, String type) {
        super(name);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .put("type", type)
                .toString();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
