package parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ast.AST;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.ASTExpr;
import ast.expr.ArithExpr;
import ast.expr.ClassExpr;
import ast.expr.FieldExpr;
import ast.expr.IntExpr;
import ast.expr.MethodExpr;
import ast.expr.NullExpr;
import ast.expr.OPExpr;
import ast.expr.ThisExpr;
import ast.expr.TypedVarExpr;
import ast.expr.VarExpr;
import ast.stmt.ASTStmt;
import ast.stmt.AssignStmt;
import ast.stmt.IfStmt;
import ast.stmt.PrintStmt;
import ast.stmt.ReturnStmt;
import ast.stmt.UpdateStmt;
import ast.stmt.WhileStmt;

public class Parser {
    private final Iterator<String> lines;
    private final List<Character> ops = Arrays.asList(new Character[] { '+', '-', '*', '/' });

    public Parser(ArrayList<String> source) {
        this.lines = source.iterator();
    }

    public AST parse() {
        String line;
        AST p = new AST();

        while (lines.hasNext()) {
            line = lines.next();
            if (line.isEmpty()) {
                continue;
            } else if (line.startsWith("main")) {
                parseProgram(p, line);
            } else if (line.startsWith("class")) {
                p.addClass(parseClassDecl(line));
            } else {
                p.addStatement(parseStmt(line));
            }
        }

        return p;
    }

    private ClassDecl parseClassDecl(String line) {
        String[] parts = line.split(" ");
        String className = parts[1];
        ClassDecl classDecl = new ClassDecl(className);
        line = lines.next().stripLeading();
        parseClassFields(classDecl, line);
        line = lines.next().stripLeading();
        parseMethodDecls(classDecl, line);
        return classDecl;
    }

    private void parseClassFields(ClassDecl classDecl, String line) {
        ParsePair pair;
        if (line.startsWith("fields")) {
            line = line.replace("fields", "");
            if (!line.stripLeading().isEmpty()) {
                while (!line.isEmpty()) {
                    pair = parseTypedVarExpr(line.substring(1));
                    classDecl.addField((TypedVarExpr) pair.getNode());
                    line = pair.getLine().stripLeading();
                }
            }
        }
    }

    private void parseMethodDecls(ClassDecl classDecl, String line) {
        ParsePair pair;
        String[] parts;
        MethodDecl methodDecl = null;

        while (!line.equals("]")) {
            if (line.startsWith("method")) {
                line = "x" + "." + line.replace("method", "");
                pair = parseMethodExpr(line, true);
                line = pair.getLine().replace("returning", "");
                parts = parseVar(line.substring(1));
                methodDecl = new MethodDecl((MethodExpr) pair.getNode(), classDecl.getName(), parts[0]);
                line = parts[1].substring(1).replace("with locals", "");
                if (!line.stripLeading().stripTrailing().equals(":")) {
                    while (!line.isEmpty() && !line.equals(":")) {
                        pair = parseTypedVarExpr(line.substring(1));
                        methodDecl.addLocalVar((TypedVarExpr) pair.getNode());
                        line = pair.getLine().stripLeading();
                    }
                }
                classDecl.addMethod(methodDecl);

            } else if (methodDecl != null) {
                methodDecl.addStatement(parseStmt(line));
            }
            line = lines.next().stripLeading();
        }
    }

    private void parseProgram(AST p, String line) {
        if (line.length() == 5) {
            return;
        }

        ParsePair pair;
        line = line.split("with")[1];

        if (!line.stripLeading().stripTrailing().equals(":")) {
            while (!line.equals(":") && !line.isBlank()) {
                pair = parseTypedVarExpr(line.substring(1));
                line = pair.getLine().stripLeading().stripTrailing();
                p.addVar((TypedVarExpr) pair.getNode());
            }
        }
    }

    private ASTStmt parseStmt(String line) {
        String[] parts;
        line = line.stripLeading();
        parts = parseVar(line);

        switch (parts[0]) {
            case "if":
                return parseIfStmt(parts[1]);
            case "ifonly":
                return parseIfOnlyStmt(parts[1]);
            case "while":
                return parseWhileStmt(parts[1]);
            case "print":
                return parsePrintStmt(parts[1].substring(1));
            case "return":
                return parseReturnStmt(parts[1]);
        }

        if (line.charAt(0) == '!') {
            return parseUpdateStmt(line);
        }
        return parseEqualStmt(parts[0], parts[1]);
    }

    private ParsePair parseExpr(String line) {
        line = line.stripLeading();
        char first = line.charAt(0);

        if (Character.isDigit(first))
            return parseIntExpr(line);
        else if (line.startsWith("null"))
            return parseNullExpr(line);
        else if (Character.isLetter(first))
            return parseVarExpr(line);
        else if (first == '(')
            return parseArithExpr(line.substring(1));
        else if (ops.contains(first))
            return new ParsePair(new OPExpr(first), line.substring(1));
        else if (first == '^')
            return parseMethodExpr(line.substring(1), false);
        else if (first == '&')
            return parseFieldExpr(line.substring(1));
        else if (first == '@')
            return parseClassExpr(line.substring(1));

        return null;
    }

    private String[] parseInt(String line) {
        line = line.stripLeading();
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
        line = line.stripLeading();
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

    private ASTStmt parseEqualStmt(String var, String line) {
        line = line.stripLeading();
        String[] parts = line.split("=", 2);
        ParsePair pair = parseExpr(parts[1]);
        return new AssignStmt(var, pair.getNode());
    }

    private ASTStmt parseUpdateStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line.substring(1));
        ASTExpr caller = pair.getNode();
        String[] parts = parseVar(pair.getLine().substring(1));
        String field = parts[0];
        pair = parseExpr(parts[1].substring(2));
        ASTExpr newVal = pair.getNode();
        return new UpdateStmt(caller, newVal, field);
    }

    private ASTStmt parseIfStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
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

    private ASTStmt parseIfOnlyStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        ASTExpr cond = pair.getNode();
        IfStmt ifStmt = new IfStmt(cond);

        line = lines.next();
        while (!line.strip().equals("}")) {
            ifStmt.addStatementToIf(parseStmt(line));
            line = lines.next();
        }

        return ifStmt;
    }

    private ASTStmt parseWhileStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        ASTExpr cond = pair.getNode();
        WhileStmt whileStmt = new WhileStmt(cond);

        line = lines.next();
        while (!line.strip().equals("}")) {
            whileStmt.addStatement(parseStmt(line));
            line = lines.next();
        }

        return whileStmt;
    }

    private ASTStmt parseReturnStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        return new ReturnStmt(pair.getNode());
    }

    private ASTStmt parsePrintStmt(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        return new PrintStmt(pair.getNode());
    }

    private ParsePair parseIntExpr(String line) {
        line = line.stripLeading();
        String[] parts = parseInt(line);
        ASTExpr node = new IntExpr(parts[0]);
        return new ParsePair(node, parts[1]);
    }

    private ParsePair parseNullExpr(String line) {
        line = line.stripLeading();
        ParsePair pair = parseTypedVarExpr(line);
        ASTExpr node = new NullExpr(((TypedVarExpr) pair.getNode()).getType());
        return new ParsePair(node, pair.getLine());
    }

    private ParsePair parseVarExpr(String line) {
        line = line.stripLeading();
        String[] parts = parseVar(line);
        ASTExpr node;

        if (parts[0].equals("this")) {
            node = new ThisExpr();
        } else {
            node = new VarExpr(parts[0]);
        }

        return new ParsePair(node, parts[1]);
    }

    private ParsePair parseArithExpr(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        ASTExpr expr1 = pair.getNode();
        pair = parseExpr(pair.getLine());
        ASTExpr op = pair.getNode();
        pair = parseExpr(pair.getLine());
        ASTExpr expr2 = pair.getNode();
        ASTExpr node = new ArithExpr(op, expr1, expr2);
        return new ParsePair(node, pair.getLine().substring(1));
    }

    private ParsePair parseMethodExpr(String line, boolean parseTypes) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        ASTExpr caller = pair.getNode();
        String[] parts = parseVar(pair.getLine().substring(1));
        MethodExpr method = new MethodExpr(parts[0], caller);
        String temp = parts[1].substring(1);

        while (!temp.isEmpty() && temp.charAt(0) != ')' && pair.getLine().charAt(0) != ')') {
            if (parseTypes) {
                pair = parseTypedVarExpr(temp);
                method.addArg((TypedVarExpr) pair.getNode());
                if (pair.getLine().length() < 2) {
                    break;
                }
                temp = pair.getLine().substring(1);
            } else {
                pair = parseExpr(temp);
                method.addArg(pair.getNode());
                if (pair.getLine().length() < 2) {
                    break;
                }
                temp = pair.getLine().substring(1);
            }
        }

        return new ParsePair(method, temp);
    }

    private ParsePair parseFieldExpr(String line) {
        line = line.stripLeading();
        ParsePair pair = parseExpr(line);
        ASTExpr caller = pair.getNode();
        String[] parts = parseVar(pair.getLine().substring(1));
        FieldExpr field = new FieldExpr(parts[0], caller);
        return new ParsePair(field, parts[1]);
    }

    private ParsePair parseClassExpr(String line) {
        line = line.stripLeading();
        String[] parts = parseVar(line);
        ASTExpr node = new ClassExpr(parts[0]);
        return new ParsePair(node, parts[1]);
    }

    private ParsePair parseTypedVarExpr(String line) {
        line = line.stripLeading();
        String[] parts = parseVar(line);
        String var = parts[0];
        parts = parseVar(parts[1].substring(1));
        String type = parts[0];
        return new ParsePair(new TypedVarExpr(var, type), parts[1]);
    }
}
