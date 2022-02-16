package cfg.primitives;

import java.util.ArrayList;

import visitor.CFGVisitor;

public class CallPrimitive extends Primitive {
    private Primitive codeAddress;
    private Primitive caller;
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

    public void setCodeAddress(Primitive codeAddress) {
        this.codeAddress = codeAddress;
    }

    public void setCaller(Primitive caller) {
        this.caller = caller;
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
