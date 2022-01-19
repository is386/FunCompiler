package ir.primitives;

public class VarPrimitive extends Primitive {
    private String name;

    public VarPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name;
    }
}
