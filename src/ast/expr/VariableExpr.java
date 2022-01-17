package ast.expr;

public class VariableExpr extends ASTExpr {
    private final String name;

    public VariableExpr(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
