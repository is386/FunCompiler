package ast;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.decl.ClassDecl;
import ast.expr.TypedVarExpr;
import ast.stmt.ASTStmt;
import cfg.CFGBuilder;

public class AST extends ASTNode {
    private ArrayList<TypedVarExpr> localVars = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();
    private ArrayList<ClassDecl> classes = new ArrayList<>();

    public AST() {
    }

    public ArrayList<TypedVarExpr> getLocalVars() {
        return localVars;
    }

    public void addVar(TypedVarExpr lv) {
        localVars.add(lv);
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

    public ArrayList<ClassDecl> getClasses() {
        return classes;
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName());

        JSONArray jClasses = new JSONArray();
        for (ClassDecl c : classes) {
            jClasses.put(new JSONObject(c.toString()));
        }

        JSONArray jVars = new JSONArray();
        for (TypedVarExpr lv : localVars) {
            jVars.put(new JSONObject(lv.toString()));
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

    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }
}
