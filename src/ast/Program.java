package ast;

import java.util.ArrayList;

import ast.decl.ClassDecl;
import ast.stmt.ASTStmt;

public class Program {
    private ArrayList<String> localVars = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();
    private ArrayList<ClassDecl> classes = new ArrayList<>();

    public Program() {
    }

    public void addVar(String lv) {
        localVars.add(lv);
    }

    public void addStatement(ASTStmt s) {
        statements.add(s);
    }

    public void addClass(ClassDecl c) {
        classes.add(c);
    }

    public String toString() {
        String s = "";
        for (ClassDecl c : classes) {
            s += c;
        }

        s += "main";

        if (!localVars.isEmpty()) {
            s += " with ";
            for (String lv : localVars) {
                s += lv + ", ";
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
