package ast.expr;

import java.util.ArrayList;

public class MethodExpr extends ASTExpr {
    private final String name;
    private final ASTExpr caller;
    private ArrayList<ASTExpr> args = new ArrayList<>();

    public MethodExpr(String name, ASTExpr caller) {
        this.name = name;
        this.caller = caller;
    }

    public String getName() {
        return name;
    }

    public ASTExpr getCaller() {
        return caller;
    }

    public void addArg(ASTExpr arg) {
        args.add(arg);
    }

    public ArrayList<ASTExpr> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        String str = "^" + caller + "." + name + "(";

        if (!args.isEmpty()) {
            for (ASTExpr expr : args) {
                str += expr + ", ";
            }
            return str.substring(0, str.length() - 2) + ")";
        }

        return str + ")";
    }
}
