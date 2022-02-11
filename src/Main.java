import java.util.ArrayList;
import java.util.Scanner;

import ast.AST;
import cfg.CFG;
import cfg.CFGBuilder;
import cfg.Dom;
import parse.Parser;
import ssa.SSAOptimized;
import ssa.SSAUnoptimized;
import visitor.CFGVisitor;

// TODO: Fix bugs found in crazy.441

public class Main {
    private static boolean doOpt = true;
    private static boolean oldSSA = false;

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-noopt")) {
                doOpt = false;
            } else if (args[i].toLowerCase().equals("-oldssa")) {
                oldSSA = true;
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

        CFGBuilder cfgBuilder = new CFGBuilder(doOpt);
        CFG cfg = cfgBuilder.build(ast);
        Dom.storeDominators(cfg);

        CFGVisitor ssa;
        if (oldSSA) {
            ssa = new SSAUnoptimized();
        } else {
            ssa = new SSAOptimized();
        }
        ssa.visit(cfg);

        System.out.println(cfg);
    }
}
