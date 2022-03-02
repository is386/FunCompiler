import java.util.ArrayList;
import java.util.Scanner;

import ast.AST;
import cfg.*;
import parse.Parser;
import ssa.SSAOptimized;
import ssa.SSAUnoptimized;
import types.TypeChecker;
import visitor.CFGVisitor;
import vn.ValueNumbering;

public class Main {
    private static boolean oldSSA = false;
    private static boolean doVN = true;

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-oldssa")) {
                oldSSA = true;
            } else if (args[i].toLowerCase().equals("-novn")) {
                doVN = false;
            }
        }
    }

    private static ArrayList<String> parseInput() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> inputList = new ArrayList<>();

        String firstLine = scanner.nextLine();

        if (firstLine.length() == 0) {
            scanner.close();
            return inputList;
        }

        inputList.add(firstLine);
        while (scanner.hasNextLine()) {
            inputList.add(scanner.nextLine().stripLeading());
        }

        scanner.close();
        return inputList;
    }

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        ArrayList<String> source = parseInput();

        Parser parser = new Parser(source);
        AST ast = parser.parse();

        TypeChecker typeChecker = new TypeChecker();
        typeChecker.visit(ast);

        CFGBuilder cfgBuilder = new CFGBuilder(typeChecker.getFieldOffsets(), typeChecker.getMethodVarTypes());
        CFG cfg = cfgBuilder.build(ast);
        Dom.storeDominators(cfg);

        CFGVisitor ssa;
        if (oldSSA) {
            ssa = new SSAUnoptimized();
        } else {
            ssa = new SSAOptimized();
        }
        ssa.visit(cfg);

        ValueNumbering vn = new ValueNumbering();
        if (doVN) {
            vn.visit(cfg);
        }

        System.out.println(cfg);
    }
}
