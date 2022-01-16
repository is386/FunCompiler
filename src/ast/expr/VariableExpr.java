package ast.expr;

public class VariableExpr extends ASTExpr {
    private final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
