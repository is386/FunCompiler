import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Parser {
    private final Iterator<String> lines;
    private final List<Character> ops = Arrays.asList(new Character[] { '+', '-', '*', '/' });

    public Parser(ArrayList<String> source) {
        this.lines = source.iterator();
    }

    public void parse() {
        Response resp;
        while (lines.hasNext()) {
            resp = parseExpr(lines.next());
            if (resp != null) {
                System.out.println(resp.getNode().toString());
            }
        }
    }

    private Response parseExpr(String line) {
        line = line.stripLeading();
        char first = line.charAt(0);
        String[] parts;
        ASTExpr node;
        Response resp;

        if (Character.isDigit(first)) {
            parts = parseInt(line);
            node = new IntegerExpr(parts[0]);
            return new Response(node, parts[1]);
        } else if (Character.isLetter(first)) {
            parts = parseVar(line);
            node = new VariableExpr(parts[0]);
            return new Response(node, parts[1]);
        } else if (first == '(') {
            resp = parseExpr(line.substring(1));
            ASTExpr expr1 = resp.getNode();
            resp = parseExpr(resp.getLine());
            ASTExpr op = resp.getNode();
            resp = parseExpr(resp.getLine());
            ASTExpr expr2 = resp.getNode();
            node = new ArithExpr(op, expr1, expr2);
            return new Response(node, resp.getLine().substring(1));
        } else if (ops.contains(first)) {
            node = new OPExpr(first);
            return new Response(node, line.substring(1));
        }
        return null;
    }

    private String[] parseInt(String line) {
        String integer = "";
        char c;
        int i;
        for (i = 0; i < line.length(); i++) {
            c = line.charAt(i);
            if (!Character.isDigit(c)) {
                break;
            }
            integer += c;
        }
        if (integer.length() == line.length()) {
            return new String[] { integer, "" };
        } else {
            return new String[] { integer, line.substring(i) };
        }
    }

    private String[] parseVar(String line) {
        String var = "";
        char c;
        int i;
        for (i = 0; i < line.length(); i++) {
            c = line.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                break;
            }
            var += c;
        }
        if (var.length() == line.length()) {
            return new String[] { var, "" };
        } else {
            return new String[] { var, line.substring(i) };
        }
    }
}
