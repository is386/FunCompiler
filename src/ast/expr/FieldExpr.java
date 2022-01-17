package ast.expr;

public class FieldExpr extends ASTExpr {
    private final ASTExpr caller;
    private final String name;

    public FieldExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public String toString() {
        return "&" + caller + "." + name;
    }
}
