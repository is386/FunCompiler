package ast.expr;

import org.json.JSONObject;

import ir.BasicBlock;
import ir.primitives.Primitive;
import ir.primitives.ThisPrimitive;

public class ThisExpr extends ASTExpr {

    public ThisExpr() {
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .toString();
    }

    @Override
    public Primitive toPrimitive(BasicBlock block) {
        return new ThisPrimitive();
    }

}
