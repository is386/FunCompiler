package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class IntExpr extends ASTExpr {
    private final int value;

    public IntExpr(String strInt) {
        this.value = Integer.parseUnsignedInt(strInt);
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("value", value)
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
