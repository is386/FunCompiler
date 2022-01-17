package ast.stmt;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.expr.ASTExpr;

public class IfStmt extends ASTStmt {

    private final ASTExpr cond;
    private ArrayList<ASTStmt> ifStatements = new ArrayList<>();
    private ArrayList<ASTStmt> elseStatements = new ArrayList<>();

    public IfStmt(ASTExpr cond) {
        this.cond = cond;
    }

    public void addStatementToIf(ASTStmt stmt) {
        if (stmt != null) {
            ifStatements.add(stmt);
        }
    }

    public void addStatementToElse(ASTStmt stmt) {
        if (stmt != null) {
            elseStatements.add(stmt);
        }
    }

    @Override
    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("cond", new JSONObject(cond.toString()));

        JSONArray jIfStmts = new JSONArray();
        for (ASTStmt s : ifStatements) {
            jIfStmts.put(new JSONObject(s.toString()));
        }

        JSONArray jElseStmts = new JSONArray();
        for (ASTStmt s : elseStatements) {
            jElseStmts.put(new JSONObject(s.toString()));
        }

        return j.put("if-stmts", jIfStmts)
                .put("else-stmts", jElseStmts)
                .toString();

    }

}
