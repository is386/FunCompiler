public class ClassExpr extends ASTExpr {
    private final String name;

    public ClassExpr(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "@" + name;
    }
}
