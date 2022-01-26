package ast.expr;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import cfg.CFGBuilder;

public class MethodExpr extends ASTExpr {
    private final String name;
    private final ASTExpr caller;
    private ArrayList<ASTExpr> args = new ArrayList<>();

    public MethodExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    public String getName() {
        return name;
    }

    public void addArg(ASTExpr arg) {
        if (arg != null) {
            args.add(arg);
        }
    }

    public ArrayList<ASTExpr> getArgs() {
        return args;
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name)
                .put("caller", new JSONObject(caller.toString()));

        JSONArray jArgs = new JSONArray();
        for (ASTExpr arg : args) {
            jArgs.put(new JSONObject(arg.toString()));
        }

        return j.put("args", jArgs).toString();
    }

    public void accept(CFGBuilder visitor) {
        visitor.visit(this);
    }
}
