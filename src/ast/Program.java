package ast;

import java.util.ArrayList;

import ast.expr.ASTExpr;
import ast.stmt.ASTStmt;

public class Program {
    private ArrayList<ASTExpr> localVars = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public Program() {
    }

    public void addVar(ASTExpr var) {
        localVars.add(var);
    }

    public ArrayList<ASTExpr> getVars() {
        return localVars;
    }

    public void addStatement(ASTStmt s) {
        statements.add(s);
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    public String toString() {
        String s = "main";

        if (!localVars.isEmpty()) {
            s += " with ";
            for (ASTExpr var : localVars) {
                s += var + ", ";
            }
            s = s.substring(0, s.length() - 2);
        }

        s += ":\n";

        for (ASTStmt stmt : statements) {
            s += stmt + "\n\n";
        }

        return s;
    }
}
