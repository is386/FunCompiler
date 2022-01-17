package ast.expr;

public class ClassExpr extends ASTExpr {
    private final String name;

    public ClassExpr(String name) {
        this.name = name;
    }

    public String toString() {
        return "@" + name;
    }
}
