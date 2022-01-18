package ast.expr;

import org.json.JSONObject;

import ir.CFGVisitor;

public class IntegerExpr extends ASTExpr {
    private final int value;

    public IntegerExpr(String strInt) {
        this.value = Integer.parseUnsignedInt(strInt);
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("value", value)
                .toString();
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
