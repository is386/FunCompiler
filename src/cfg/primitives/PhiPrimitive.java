package cfg.primitives;

import java.util.LinkedHashMap;

import visitor.CFGVisitor;

public class PhiPrimitive extends Primitive {

    private LinkedHashMap<String, Primitive> varMap;

    public PhiPrimitive() {
    }

    public LinkedHashMap<String, Primitive> getVarMap() {
        return varMap;
    }

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

    @Override
    public int hashCode() {
        return 5;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PhiPrimitive)) {
            return false;
        }
        PhiPrimitive i = (PhiPrimitive) o;
        return i.getVarMap().equals(this.varMap);
    }
}
