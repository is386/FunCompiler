import java.util.ArrayList;
import java.util.Scanner;

import ast.AST;
import cfg.CFG;
import cfg.CFGBuilder;
import parse.Parser;
import ssa.SSA2;

public class Main {
    private static boolean doOpt = true;

    private static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].toLowerCase().equals("-noopt")) {
                doOpt = false;
            }
        }
    }

    private static ArrayList<String> parseInput() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> inputList = new ArrayList<>();

        System.out.println("Input Program (Press Enter to Exit):\n");
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

        // SSATransformer ssaTransformer = new SSATransformer();
        // ssaTransformer.visit(cfg);

        SSA2 ssa2 = new SSA2();
        ssa2.visit(cfg);

        System.out.println(cfg);
    }
}
