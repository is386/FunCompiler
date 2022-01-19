package ast.decl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ast.ASTNode;
import ir.CFGVisitor;

public class ClassDecl extends ASTNode {
    private final String name;
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<MethodDecl> methods = new ArrayList<>();

    public ClassDecl(String name) {
        this.name = name;
    }

    public void addField(String f) {
        if (!f.isEmpty()) {
            fields.add(f);
        }
    }

    public void addMethod(MethodDecl m) {
        if (m != null) {
            methods.add(m);
        }
    }

    public String toString() {
        JSONObject j = new JSONObject()
                .put("node", this.getClass().getSimpleName())
                .put("name", name);

        JSONArray jFields = new JSONArray();
        for (String f : fields) {
            jFields.put(f);
        }

        JSONArray jMethods = new JSONArray();
        for (MethodDecl m : methods) {
            jMethods.put(new JSONObject(m.toString()));
        }

        return j
                .put("fields", jFields)
                .put("methods", jMethods)
                .toString();
    }

    @Override
    public void accept(CFGVisitor visitor) {
        visitor.visit(this);
    }
}
