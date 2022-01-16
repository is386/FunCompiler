package ast.decl;

import java.util.ArrayList;

import ast.expr.ASTExpr;
import ast.stmt.ASTStmt;

public class MethodDecl {
    private final ASTExpr method;
    private ArrayList<ASTExpr> localVars = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public MethodDecl(ASTExpr method) {
        this.method = method;
    }

    public ASTExpr getMethod() {
        return method;
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
        String s = "method " + method.toString() + " with locals ";

        if (!localVars.isEmpty()) {
            for (ASTExpr var : localVars) {
                s += var + ", ";
            }
            s = s.substring(0, s.length() - 2);
        }

        s += ":\n";
        for (ASTStmt stmt : statements) {
            s += stmt + "\n";
        }

        return s + "\n";
    }
}
