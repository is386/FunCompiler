package ir.primitives;

public class VariablePrimitive extends Primitive {
    private final String name;

    public VariablePrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name;
    }

    @Override
    public Primitive getVar() {
        return this;
    }
}
