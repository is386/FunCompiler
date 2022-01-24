package cfg.primitives;

public class GetEltPrimitive extends Primitive {
    private final Primitive primitive;
    private final Primitive offset;

    public GetEltPrimitive(Primitive p, Primitive offset) {
        this.primitive = p;
        this.offset = offset;
    }

    public String toString() {
        return String.format("getelt(%s, %s)", primitive, offset);
    }
}
