package ast.stmt;

import ast.expr.ASTExpr;

public class UpdateStmt extends ASTStmt {
    private final ASTExpr caller;
    private final ASTExpr newVal;
    private final String name;

    public UpdateStmt(ASTExpr caller, ASTExpr newVal, String name) {
        this.caller = caller;
        this.newVal = newVal;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    public ASTExpr getNewVal() {
        return newVal;
    }

    @Override
    public String toString() {
        return "!" + caller.toString() + "." + name + " = " + newVal.toString();
    }
}
