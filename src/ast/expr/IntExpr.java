package ast.expr;

import org.json.JSONObject;

import cfg.CFGBuilder;

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

    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }
}
