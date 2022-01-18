package ast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.decl.ClassDecl;
import ast.stmt.ASTStmt;
import ir.CFGVisitor;

public class Program extends ASTNode {
    private ArrayList<String> localVars = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();
    private ArrayList<ClassDecl> classes = new ArrayList<>();

    public Program() {
    }

    public void addVar(String lv) {
        if (!lv.isEmpty()) {
            localVars.add(lv);
        }
    }

    public void addStatement(ASTStmt s) {
        if (s != null) {
            statements.add(s);
        }
    }

    public void addClass(ClassDecl c) {
        if (c != null) {
            classes.add(c);
        }
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName());

        JSONArray jClasses = new JSONArray();
        for (ClassDecl c : classes) {
            jClasses.put(new JSONObject(c.toString()));
        }

        JSONArray jVars = new JSONArray();
        for (String lv : localVars) {
            jVars.put(lv);
        }

        JSONArray jStmts = new JSONArray();
        for (ASTStmt s : statements) {
            jStmts.put(new JSONObject(s.toString()));
        }

        return j
                .put("classes", jClasses)
                .put("local-vars", jVars)
                .put("stmts", jStmts)
                .toString();
    }

    @Override
    public void accept(CFGVisitor visitor) {
    }
}
