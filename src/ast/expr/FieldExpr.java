package ast.expr;

import org.json.JSONObject;

import ir.BasicBlock;
import ir.primitives.Primitive;

public class FieldExpr extends ASTExpr {
    private final ASTExpr caller;
    private final String name;

    public FieldExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .put("caller", new JSONObject(caller.toString()))
                .toString();
    }

    @Override
    public Primitive toPrimitive(BasicBlock block) {
        // TODO Auto-generated method stub
        return null;
    }
}
