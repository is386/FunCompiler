import java.util.ArrayList;
import java.util.Scanner;

import ast.AST;
import cfg.CFG;
import cfg.CFGBuilder;
import cfg.Dom;
import parse.Parser;
import ssa.SSATransformer;

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

        // HashSet<String> names = new HashSet<>();
        // ArrayList<BasicBlock> newBlocks = new ArrayList<>();
        // for (int i = 0; i < 9; i++) {
        // names.add("l" + Integer.toString(i));
        // newBlocks.add(new BasicBlock("l" + Integer.toString(i), i));
        // }
        // newBlocks.get(1).addParent(newBlocks.get(0));
        // newBlocks.get(1).addParent(newBlocks.get(3));
        // newBlocks.get(2).addParent(newBlocks.get(1));
        // newBlocks.get(3).addParent(newBlocks.get(2));
        // newBlocks.get(3).addParent(newBlocks.get(7));
        // newBlocks.get(4).addParent(newBlocks.get(3));
        // newBlocks.get(5).addParent(newBlocks.get(1));
        // newBlocks.get(6).addParent(newBlocks.get(5));
        // newBlocks.get(7).addParent(newBlocks.get(6));
        // newBlocks.get(7).addParent(newBlocks.get(8));
        // newBlocks.get(8).addParent(newBlocks.get(5));
        // CFG cfg2 = new CFG();
        // cfg2.setBlocks(newBlocks);
        // cfg2.incrNumFuncs();
        // cfg2.setFuncBlocks(names);
        // Dom.storeDominators(cfg2);
        Dom.storeDominators(cfg);

        SSATransformer ssa = new SSATransformer();
        ssa.visit(cfg);

        System.out.println(cfg);
    }
}
