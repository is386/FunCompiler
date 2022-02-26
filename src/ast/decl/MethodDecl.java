package ast.decl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.ASTNode;
import ast.expr.*;
import ast.stmt.ASTStmt;
import visitor.ASTVisitor;

public class MethodDecl extends ASTNode {
    private final String name;
    private final String returnType;
    private String className;
    private ArrayList<TypedVarExpr> localVars = new ArrayList<>();
    private ArrayList<TypedVarExpr> args = new ArrayList<>();
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public MethodDecl(MethodExpr methodExpr, String className, String returnType) {
        this.name = methodExpr.getName();
        this.returnType = returnType;
        this.args.add(new TypedVarExpr("this", className));
        for (ASTExpr e : methodExpr.getArgs()) {
            if (e != null) {
                args.add((TypedVarExpr) e);
            }
        }
    }

    public String getReturnType() {
        return returnType;
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    public void addLocalVar(TypedVarExpr lv) {
        localVars.add(lv);
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

    public ArrayList<TypedVarExpr> getLocalVars() {
        return localVars;
    }

    public ArrayList<TypedVarExpr> getArgs() {
        return args;
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .put("return", returnType);

        JSONArray jArgs = new JSONArray();
        for (TypedVarExpr a : args) {
            jArgs.put(new JSONObject(a.toString()));
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
                .put("args", jArgs)
                .put("local-vars", jVars)
                .put("stmts", jStmts)
                .toString();
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
