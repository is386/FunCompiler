public class OPExpr extends ASTExpr {

    private final Character op;

    public OPExpr(Character op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return op.toString();
    }
}
