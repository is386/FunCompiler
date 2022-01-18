package ir;

import java.util.ArrayList;

import ast.Program;
import ast.stmt.ASTStmt;

public class CFGBuilder {

    private final Program program;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();

    public CFGBuilder(Program p) {
        program = p;
    }

    public void build() {
        BasicBlock block = new BasicBlock("main", 0);
        for (ASTStmt stmt : program.getStatements()) {
            stmt.buildBlock(block);
        }
    }
}
