package ast.stmt;

import ir.BasicBlock;

public abstract class ASTStmt {

    public abstract String toString();

    public abstract void buildBlock(BasicBlock block);
}
