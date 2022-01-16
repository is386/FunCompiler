package stmt;

import java.util.ArrayList;

import expr.ASTExpr;

public class IfStmt extends ASTStmt {

    private final ASTExpr cond;
    private ArrayList<ASTStmt> ifStatements = new ArrayList<>();
    private ArrayList<ASTStmt> elseStatements = new ArrayList<>();

    public IfStmt(ASTExpr cond) {
        this.cond = cond;
    }

    public ASTExpr getCond() {
        return cond;
    }

    public ArrayList<ASTStmt> getIfStatements() {
        return ifStatements;
    }

    public ArrayList<ASTStmt> getElseStatements() {
        return elseStatements;
    }

    public void addStatementToIf(ASTStmt stmt) {
        ifStatements.add(stmt);
    }

    public void addStatementToElse(ASTStmt stmt) {
        elseStatements.add(stmt);
    }

    @Override
    public String toString() {
        String s = "";
        if (elseStatements.isEmpty()) {
            s += "ifonly " + cond.toString() + ": {\n";
        } else {
            s += "if " + cond.toString() + ": {\n";
        }

        for (ASTStmt stmt : ifStatements) {
            s += stmt.toString() + "\n";
        }

        if (!elseStatements.isEmpty()) {
            s += "} else {\n";
            for (ASTStmt stmt : elseStatements) {
                s += stmt.toString() + "\n";
            }
        }
        s += "}";
        return s;
    }

}
