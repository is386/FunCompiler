package cfg.primitives;

import cfg.Type;
import ssa.SSAVisitor;

public class ArithPrimitive extends Primitive {

    private Primitive operand1;
    private Primitive operand2;
    private final String op;

    public ArithPrimitive(String op) {
        this.op = op;
    }

    public Primitive getOperand1() {
        return operand1;
    }

    public Primitive getOperand2() {
        return operand2;
    }

    public void setOperand1(Primitive p) {
        operand1 = p;
    }

    public void setOperand2(Primitive p) {
        operand2 = p;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", operand1, op, operand2);
    }

    @Override
    public Type getType() {
        return Type.INTEGER;
    }

    @Override
    public void accept(SSAVisitor visitor) {
        visitor.visit(this);
    }
}
