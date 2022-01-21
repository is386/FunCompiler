package ir.stmt;

public class ControlJump extends ControlStmt {
    private final String name;

    public ControlJump(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "    jump " + name;
    }
}
