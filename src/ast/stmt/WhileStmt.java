package ast.stmt;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.expr.ASTExpr;
import cfg.CFGVisitor;

public class WhileStmt extends ASTStmt {

    private final ASTExpr cond;
    private ArrayList<ASTStmt> statements = new ArrayList<>();

    public WhileStmt(ASTExpr cond) {
        this.cond = cond;
    }

    public ASTExpr getCond() {
        return cond;
    }

    public ArrayList<ASTStmt> getStatements() {
        return statements;
    }

    public void addStatement(ASTStmt stmt) {
        if (stmt != null) {
            statements.add(stmt);
        }
    }

    @Override
    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("cond", new JSONObject(cond.toString()));

        JSONArray jStmts = new JSONArray();
        for (ASTStmt s : statements) {
            jStmts.put(new JSONObject(s.toString()));
        }

        return j.put("stmts", jStmts).toString();
    }

    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
