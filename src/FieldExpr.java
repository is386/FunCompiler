public class FieldExpr extends ASTExpr {
    private final ASTExpr caller;
    private final String name;

    public FieldExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public String getName() {
        return name;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    @Override
    public String toString() {
        return name;
    }
}
