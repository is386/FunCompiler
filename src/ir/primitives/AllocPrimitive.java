package ir.primitives;

public class AllocPrimitive extends Primitive {
    private final int space;

    public AllocPrimitive(int space) {
        this.space = space;
    }

    public String toString() {
        return "alloc(" + Integer.toString(space) + ")";
    }
}
