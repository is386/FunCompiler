package ast.expr;

import org.json.JSONObject;

import ir.CFGVisitor;

public class FieldExpr extends ASTExpr {
    private final ASTExpr caller;
    private final String name;

    public FieldExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .put("caller", new JSONObject(caller.toString()))
                .toString();
    }

    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
