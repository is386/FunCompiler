package ast.decl;

import java.util.ArrayList;

import ast.expr.ASTExpr;
import ast.expr.MethodExpr;
import ast.stmt.ASTStmt;

public class MethodDecl {
    private final String name;
    private ArrayList<String> localVars = new ArrayList<>();
    private ArrayList<String> args = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public MethodDecl(MethodExpr methodExpr) {
        this.name = methodExpr.getName();
        for (ASTExpr e : methodExpr.getArgs()) {
            args.add(e.getName());
        }
    }

    public void addLocalVar(String lv) {
        localVars.add(lv);
    }

    public void addStatement(ASTStmt s) {
        statements.add(s);
    }

    public String toString() {
        String s = "method " + name + " with locals ";

        if (!localVars.isEmpty()) {
            for (String lv : localVars) {
                s += lv + ", ";
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
