package ast.expr;

import org.json.JSONObject;

import ir.BasicBlock;
import ir.primitives.Primitive;

public class ClassExpr extends ASTExpr {
    private final String name;

    public ClassExpr(String name) {
        this.name = name;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .toString();
    }

    @Override
    public Primitive toPrimitive(BasicBlock block) {
        // TODO Auto-generated method stub
        return null;
    }
}
