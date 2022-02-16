package cfg.primitives;

import cfg.Type;
import visitor.CFGVisitor;

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

    public String getOp() {
        return this.op;
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
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return 4;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ArithPrimitive)) {
            return false;
        }
        ArithPrimitive i = (ArithPrimitive) o;
        return i.getOp().equals(this.op) &&
                ((i.getOperand1().equals(this.operand1) &&
                        i.getOperand2().equals(this.operand2)) ||
                        (i.getOperand2().equals(this.operand1) &&
                                i.getOperand1().equals(this.operand2)));
    }
}
