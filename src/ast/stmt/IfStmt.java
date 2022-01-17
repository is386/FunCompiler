package ast.stmt;

import java.util.ArrayList;

import ast.expr.ASTExpr;

public class IfStmt extends ASTStmt {

    private final ASTExpr cond;
    private ArrayList<ASTStmt> ifStatements = new ArrayList<>();
    private ArrayList<ASTStmt> elseStatements = new ArrayList<>();

    public IfStmt(ASTExpr cond) {
        this.cond = cond;
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
            s += "ifonly " + cond + ": {\n";
        } else {
            s += "if " + cond + ": {\n";
        }

        for (ASTStmt stmt : ifStatements) {
            s += stmt + "\n";
        }

        if (!elseStatements.isEmpty()) {
            s += "} else {\n";
            for (ASTStmt stmt : elseStatements) {
                s += stmt + "\n";
            }
        }
        s += "}";
        return s;
    }

}
