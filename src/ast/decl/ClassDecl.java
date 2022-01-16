package ast.decl;

import java.util.ArrayList;

import ast.expr.ASTExpr;

public class ClassDecl {
    private final String name;
    private ArrayList<ASTExpr> fields = new ArrayList<>();
    private ArrayList<MethodDecl> methodDecls = new ArrayList<>();

    public ClassDecl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ASTExpr> getFields() {
        return fields;
    }

    public ArrayList<MethodDecl> getMethodDecls() {
        return methodDecls;
    }

    public void addField(ASTExpr var) {
        fields.add(var);
    }

    public void addMethod(MethodDecl method) {
        methodDecls.add(method);
    }

    public String toString() {
        String s = "class " + name + " [\n";

        if (!fields.isEmpty()) {
            s += "fields ";
            for (ASTExpr var : fields) {
                s += var + ", ";
            }
            s = s.substring(0, s.length() - 2);
        }

        s += "\n";
        for (MethodDecl m : methodDecls) {
            s += m;
        }
        return s + "]\n\n";
    }
}
