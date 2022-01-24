package cfg.stmt;

import java.util.ArrayList;

public class IRData extends IRStmt {

    private String name;
    private ArrayList<Object> data;

    public IRData(String name, ArrayList<Object> data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String toString() {
        String s = "global array " + name + ": ";
        s += data.toString().replace("[", "{ ").replace("]", " }");
        return s;
    }
}
