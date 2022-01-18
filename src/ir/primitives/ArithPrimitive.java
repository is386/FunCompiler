package ir.primitives;

public class ArithPrimitive extends Primitive {
    private final Primitive prim1;
    private final Primitive prim2;
    private final String op;
    private final Primitive var;

    public ArithPrimitive(Primitive prim1, Primitive prim2, String op, Primitive var) {
        this.prim1 = prim1;
        this.prim2 = prim2;
        this.op = op;
        this.var = var;
    }

    public String toString() {
        return prim1 + " " + op + " " + prim2;
    }

    public Primitive getVar() {
        return var;
    }
}
