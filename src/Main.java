import java.util.ArrayList;
import java.util.Scanner;

import ast.Program;
import cfg.BasicBlock;
import cfg.CFGVisitor;
import cfg.stmt.ControlCond;
import cfg.stmt.ControlJump;
import cfg.stmt.ControlReturn;
import parse.Parser;

public class Main {
    public static ArrayList<String> parseInput() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> inputList = new ArrayList<>();

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
        CFGVisitor cfgVisitor = new CFGVisitor();
        program.accept(cfgVisitor);
        for (BasicBlock b : cfgVisitor.getBlocks()) {
            if (b.getControlStmt() instanceof ControlCond) {
                System.out.println("cond");
            } else if (b.getControlStmt() instanceof ControlJump) {
                System.out.println("jump");
            } else if (b.getControlStmt() instanceof ControlReturn) {
                System.out.println("return");
            }
        }
        // for (BasicBlock b : cfgVisitor.getBlocks()) {
        // System.out.println(b);
        // }
    }
}
