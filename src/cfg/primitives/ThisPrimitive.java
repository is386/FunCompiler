package cfg.primitives;

import cfg.Type;

public class ThisPrimitive extends VarPrimitive {

    public ThisPrimitive() {
        super("this");
        setType(Type.THIS);
    }
}
