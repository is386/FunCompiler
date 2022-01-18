package ast.expr;

import org.json.JSONObject;

import ir.BasicBlock;
import ir.primitives.IntegerPrimitive;
import ir.primitives.Primitive;

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
    public Primitive toPrimitive(BasicBlock block) {
        return new IntegerPrimitive(value);
    }
}
