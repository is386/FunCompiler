package ir.primitives;

public class VarPrimitive extends Primitive {
    private String name;
    private String type = "either";

    public VarPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return "%" + name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isVar() {
        return true;
    }

}
