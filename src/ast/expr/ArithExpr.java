package ast.expr;

import org.json.JSONObject;

import visitor.ASTVisitor;

public class ArithExpr extends ASTExpr {
    private final ASTExpr op;
    private final ASTExpr expr1;
    private final ASTExpr expr2;

    public ArithExpr(ASTExpr op, ASTExpr expr1, ASTExpr expr2) {
        this.op = op;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public String getOp() {
        return op.toString();
    }

    public ASTExpr getExpr1() {
        return expr1;
    }

    public ASTExpr getExpr2() {
        return expr2;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("op", op)
                .put("expr1", new JSONObject(expr1.toString()))
                .put("expr2", new JSONObject(expr2.toString()))
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
