import java.util.ArrayList;
import java.util.Scanner;

import ast.AST;
import cfg.CFG;
import cfg.CFGBuilder;
import parse.Parser;
import ssa.SSA;

// TODO: Flags for noopt, noSSA
// TODO: CFG class instead of list
// TODO: Rename and move visitors

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
        AST ast = parser.parse();

        CFGBuilder cfgBuilder = new CFGBuilder();
        CFG cfg = cfgBuilder.build(ast);

        SSA ssa = new SSA();
        ssa.visit(cfg);

        System.out.println(cfg);
    }
}
