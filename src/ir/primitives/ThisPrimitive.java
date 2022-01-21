package ir.primitives;

import ir.Type;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
        setType(Type.THIS);
    }
}
