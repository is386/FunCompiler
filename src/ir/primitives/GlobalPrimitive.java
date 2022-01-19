package ir.primitives;

public class GlobalPrimitive extends Primitive {
    private String name;

    public GlobalPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "@" + name;
    }
}
