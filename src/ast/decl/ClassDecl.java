package ast.decl;

import java.util.ArrayList;

public class ClassDecl {
    private final String name;
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<MethodDecl> methods = new ArrayList<>();

    public ClassDecl(String name) {
        this.name = name;
    }

    public void addField(String f) {
        fields.add(f);
    }

    public void addMethod(MethodDecl m) {
        methods.add(m);
    }

    public String toString() {
        String s = "class " + name + " [\n";

        if (!fields.isEmpty()) {
            s += "fields ";
            for (String f : fields) {
                s += f + ", ";
            }
            s = s.substring(0, s.length() - 2);
        }

        s += "\n";
        for (MethodDecl m : methods) {
            s += m;
        }
        return s + "]\n\n";
    }
}
