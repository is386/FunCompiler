package ast.decl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.ASTNode;
import ast.ASTVistor;
import ast.expr.ASTExpr;
import ast.expr.MethodExpr;
import ast.stmt.ASTStmt;

public class MethodDecl extends ASTNode {
    private final String name;
    private ArrayList<String> localVars = new ArrayList<>();
    private ArrayList<String> args = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public MethodDecl(MethodExpr methodExpr) {
        this.name = methodExpr.getName();
        for (ASTExpr e : methodExpr.getArgs()) {
            if (e != null) {
                args.add(e.getName());
            }
        }
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

    @Override
    public void accept(ASTVistor vistor) {
        // TODO Auto-generated method stub

    }
}
