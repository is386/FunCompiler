package cfg.stmt;

public class IRFail extends IRStmt {
    private final String failType;

    public IRFail(String failType) {
        this.failType = failType;
    }

    @Override
    public String toString() {
        return "fail " + failType;
    }
}
