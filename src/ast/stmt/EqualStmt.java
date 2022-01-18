package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import ir.BasicBlock;
import ir.primitives.Primitive;
import ir.primitives.VariablePrimitive;
import ir.stmt.IREqual;

public class EqualStmt extends ASTStmt {
    private final String var;
    private final ASTExpr expr;

    public EqualStmt(String var, ASTExpr expr) {
        this.var = var.isEmpty() ? "_" : var;
        this.expr = expr;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("var", var)
                .put("expr", new JSONObject(expr.toString()))
                .toString();
    }

    public void buildBlock(BasicBlock block) {
        VariablePrimitive varPrimitive = new VariablePrimitive(var);
        Primitive exprPrimitive = expr.toPrimitive(block);
        IREqual irStmt = new IREqual(varPrimitive, exprPrimitive);
        System.out.println(irStmt);
    }
}
