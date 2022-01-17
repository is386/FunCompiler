package ast.expr;

public class IntegerExpr extends ASTExpr {
    private final int value;

    public IntegerExpr(String strInt) {
        this.value = Integer.parseUnsignedInt(strInt);
    }

    public String toString() {
        return String.valueOf(value);
    }
}
