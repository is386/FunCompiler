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
        ASTStmt s;
        String line;
        while (lines.hasNext()) {
            line = lines.next();
            if (line.isEmpty()) {
                continue;
            }
            s = parseStmt(line);
            if (s != null) {
                System.out.println(s.toString());
                System.out.println();
            }
        }
    }

    private ASTStmt parseStmt(String line) {
        String[] parts;
        ParsePair pair;

        line = line.stripLeading();
        char first = line.charAt(0);
        if (first == '_') {
            parts = line.split("=", 2);
            pair = parseExpr(parts[1]);
            return new EqualStmt("_", pair.getNode());
        } else if (first == '!') {
            pair = parseExpr(line.substring(1));
            ASTExpr caller = pair.getNode();
            parts = parseVar(pair.getLine().substring(1));
            String field = parts[0];
            pair = parseExpr(parts[1].substring(2));
            ASTExpr newVal = pair.getNode();
            return new UpdateStmt(caller, newVal, field);
        }
        parts = parseVar(line);
        switch (parts[0]) {
            case "print":
                pair = parseExpr(parts[1]);
                return new PrintStmt(pair.getNode());
            case "return":
                pair = parseExpr(parts[1]);
                return new ReturnStmt(pair.getNode());
            case "if": {
                pair = parseExpr(parts[1]);
                ASTExpr cond = pair.getNode();
                IfStmt ifStmt = new IfStmt(cond);
                line = lines.next();
                while (!line.contains("else")) {
                    ifStmt.addStatementToIf(parseStmt(line));
                    line = lines.next();
                }
                line = lines.next();
                while (!line.strip().equals("}")) {
                    ifStmt.addStatementToElse(parseStmt(line));
                    line = lines.next();
                }
                return ifStmt;
            }
            case "ifonly": {
                pair = parseExpr(parts[1]);
                ASTExpr cond = pair.getNode();
                IfStmt ifStmt = new IfStmt(cond);
                line = lines.next();
                while (!line.strip().equals("}")) {
                    ifStmt.addStatementToIf(parseStmt(line));
                    line = lines.next();
                }
                return ifStmt;
            }
            case "while": {
                pair = parseExpr(parts[1]);
                ASTExpr cond = pair.getNode();
                WhileStmt whileStmt = new WhileStmt(cond);
                line = lines.next();
                while (!line.strip().equals("}")) {
                    whileStmt.addStatement(parseStmt(line));
                    line = lines.next();
                }
                return whileStmt;
            }
            default:
                String var = parts[0];
                parts = line.split("=", 2);
                pair = parseExpr(parts[1]);
                return new EqualStmt(var, pair.getNode());
        }
    }

    private ParsePair parseExpr(String line) {
        String[] parts;
        ASTExpr node;
        ParsePair pair;

        line = line.stripLeading();
        char first = line.charAt(0);

        if (Character.isDigit(first)) {
            parts = parseInt(line);
            node = new IntegerExpr(parts[0]);
            return new ParsePair(node, parts[1]);
        } else if (Character.isLetter(first)) {
            parts = parseVar(line);
            if (parts[0].equals("this")) {
                node = new ThisExpr();
            } else {
                node = new VariableExpr(parts[0]);
            }
            return new ParsePair(node, parts[1]);
        } else if (first == '(') {
            pair = parseExpr(line.substring(1));
            ASTExpr expr1 = pair.getNode();
            pair = parseExpr(pair.getLine());
            ASTExpr op = pair.getNode();
            pair = parseExpr(pair.getLine());
            ASTExpr expr2 = pair.getNode();
            node = new ArithExpr(op, expr1, expr2);
            return new ParsePair(node, pair.getLine().substring(1));
        } else if (ops.contains(first)) {
            node = new OPExpr(first);
            return new ParsePair(node, line.substring(1));
        } else if (first == '@') {
            parts = parseVar(line.substring(1));
            node = new ClassExpr(parts[0]);
            return new ParsePair(node, parts[1]);
        } else if (first == '^') {
            pair = parseExpr(line.substring(1));
            ASTExpr caller = pair.getNode();
            parts = parseVar(pair.getLine().substring(1));
            MethodExpr method = new MethodExpr(parts[0], caller);
            String temp = parts[1].substring(1);
            while (!temp.isEmpty() && pair.getLine().charAt(0) != ')') {
                pair = parseExpr(temp);
                method.addArg(pair.getNode());
                if (pair.getLine().length() < 2) {
                    break;
                }
                temp = pair.getLine().substring(1);
            }
            return new ParsePair(method, temp);
        } else if (first == '&') {
            pair = parseExpr(line.substring(1));
            ASTExpr caller = pair.getNode();
            parts = parseVar(pair.getLine().substring(1));
            FieldExpr field = new FieldExpr(parts[0], caller);
            return new ParsePair(field, parts[1]);
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
