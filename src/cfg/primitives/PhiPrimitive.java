package cfg.primitives;

import java.util.LinkedHashMap;

import visitor.CFGVisitor;

public class PhiPrimitive extends Primitive {

    private LinkedHashMap<String, Primitive> varMap;

    public void setVarMap(LinkedHashMap<String, Primitive> v) {
        varMap = v;
    }

    @Override
    public String toString() {
        String s = "phi(";
        if (!varMap.isEmpty()) {
            for (String k : varMap.keySet()) {
                s += k + ", " + varMap.get(k) + ", ";
            }
            s = s.substring(0, s.length() - 2);
        }
        return s + ")";
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

}
