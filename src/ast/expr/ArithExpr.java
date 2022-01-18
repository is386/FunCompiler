package ast.expr;

import org.json.JSONObject;

import ir.BasicBlock;
import ir.primitives.ArithPrimitive;
import ir.primitives.Primitive;
import ir.primitives.VariablePrimitive;

public class ArithExpr extends ASTExpr {
    private final ASTExpr op;
    private final ASTExpr expr1;
    private final ASTExpr expr2;

    public ArithExpr(ASTExpr op, ASTExpr expr1, ASTExpr expr2) {
        this.op = op;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("op", op)
                .put("expr1", new JSONObject(expr1.toString()))
                .put("expr2", new JSONObject(expr2.toString()))
                .toString();
    }

    public Primitive toPrimitive(BasicBlock block) {
        Primitive expr1Primitive = expr1.toPrimitive(block);
        Primitive expr2Primitive = expr2.toPrimitive(block);
        block.incrVarCounter();
        String varNum = Integer.toString(block.getVarCounter());
        Primitive tempVar = new VariablePrimitive(varNum);
        return new ArithPrimitive(expr1Primitive.getVar(), expr2Primitive.getVar(), op.toString(), tempVar);
    }
}
