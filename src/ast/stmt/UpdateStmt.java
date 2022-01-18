package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import ir.BasicBlock;

public class UpdateStmt extends ASTStmt {
    private final ASTExpr caller;
    private final ASTExpr newVal;
    private final String name;

    public UpdateStmt(ASTExpr caller, ASTExpr newVal, String name) {
        this.caller = caller;
        this.newVal = newVal;
        this.name = name;
    }

    @Override
    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("caller", new JSONObject(caller.toString()))
                .put("field", name)
                .put("newVal", new JSONObject(newVal.toString()))
                .toString();
    }

    @Override
    public void buildBlock(BasicBlock block) {
        // TODO Auto-generated method stub

    }
}
