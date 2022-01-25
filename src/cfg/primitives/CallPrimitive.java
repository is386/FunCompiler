package cfg.primitives;

import java.util.ArrayList;

import ssa.SSAVisitor;

public class CallPrimitive extends Primitive {
    private final Primitive codeAddress;
    private final Primitive caller;
    private ArrayList<Primitive> args = new ArrayList<>();

    public CallPrimitive(Primitive codeAddress, Primitive caller) {
        this.codeAddress = codeAddress;
        this.caller = caller;
    }

    public Primitive getCodeAddress() {
        return codeAddress;
    }

    public Primitive getCaller() {
        return caller;
    }

    public ArrayList<Primitive> getArgs() {
        return args;
    }

    public void addArg(Primitive arg) {
        if (arg != null) {
            args.add(arg);
        }
    }

    @Override
    public String toString() {
        String s = "call(" + codeAddress + ", " + caller;
        if (!args.isEmpty()) {
            for (Primitive p : args) {
                s += ", " + p;
            }
        }
        return s + ")";
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
