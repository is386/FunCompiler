package ast.stmt;

import java.util.ArrayList;

import ast.expr.ASTExpr;

public class WhileStmt extends ASTStmt {

    private final ASTExpr cond;
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public WhileStmt(ASTExpr cond) {
        this.cond = cond;
    }

    public ASTExpr getCond() {
        return cond;
    }

    public void addStatement(ASTStmt stmt) {
        statements.add(stmt);
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        String s = "while " + cond.toString() + ": {\n";
        ;
        for (ASTStmt stmt : statements) {
            s += stmt.toString() + "\n";
        }
        s += "}";
        return s;
    }
}
