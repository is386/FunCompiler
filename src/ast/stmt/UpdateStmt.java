package ast.stmt;

import org.json.JSONObject;

import ast.expr.ASTExpr;
import cfg.CFGTransformer;

public class UpdateStmt extends ASTStmt {
    private final ASTExpr caller;
    private final ASTExpr newVal;
    private final String name;

    public UpdateStmt(ASTExpr caller, ASTExpr newVal, String name) {
        this.caller = caller;
        this.newVal = newVal;
        this.name = name;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    public ASTExpr getNewVal() {
        return newVal;
    }

    public String getName() {
        return name;
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

    public void accept(CFGTransformer visitor) {
        visitor.visit(this);
    }
}
