package ir.primitives;

public class ArithPrimitive extends Primitive {

    private Primitive prim1;
    private Primitive prim2;
    private final String op;

    public ArithPrimitive(String op) {
        this.op = op;
    }

    public void setPrim1(Primitive p) {
        prim1 = p;
    }

    public void setPrim2(Primitive p) {
        prim2 = p;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", prim1, op, prim2);
    }
}
