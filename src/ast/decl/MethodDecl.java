package ast.decl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.ASTNode;
import ast.expr.ASTExpr;
import ast.expr.MethodExpr;
import ast.stmt.ASTStmt;
import cfg.CFGBuilder;

public class MethodDecl extends ASTNode {
    private final String name;
    private String className;
    private ArrayList<String> localVars = new ArrayList<>();
    private ArrayList<String> args = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public MethodDecl(MethodExpr methodExpr) {
        this.name = methodExpr.getName();
        this.args.add("this");
        for (ASTExpr e : methodExpr.getArgs()) {
            if (e != null) {
                args.add(e.getName());
            }
        }
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    public void addLocalVar(String lv) {
        if (!lv.isEmpty()) {
            localVars.add(lv);
        }
    }

    public void addStatement(ASTStmt s) {
        if (s != null) {
            statements.add(s);
        }
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String s) {
        className = s;
    }

    public String getBlockName() {
        return name + className;
    }

    public ArrayList<String> getLocalVars() {
        return localVars;
    }

    public ArrayList<String> getArgs() {
        return args;
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name);

        JSONArray jArgs = new JSONArray();
        for (String a : args) {
            jArgs.put(a);
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
                .put("args", jArgs)
                .put("local-vars", jVars)
                .put("stmts", jStmts)
                .toString();
    }

    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }
}
