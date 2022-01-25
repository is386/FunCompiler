import java.util.ArrayList;
import java.util.Scanner;

import ast.Program;
import cfg.BasicBlock;
import cfg.CFGTransformer;
import parse.Parser;
import ssa.SSATransformer;

public class Main {
    public static ArrayList<String> parseInput() {
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
        ArrayList<String> source = parseInput();
        Parser parser = new Parser(source);
        Program program = parser.parse();

        CFGTransformer cfgTransformer = new CFGTransformer();
        cfgTransformer.visit(program);
        ArrayList<BasicBlock> cfgBlocks = cfgTransformer.getBlocks();

        SSATransformer ssaTransformer = new SSATransformer();
        ssaTransformer.setTempVarCount(cfgTransformer.getTempVarCount());
        ssaTransformer.visit(cfgBlocks);

        // for (BasicBlock b : cfgBlocks) {
        // System.out.println(b);
        // }
    }
}
