package ast;

import ir.CFGVisitor;

public abstract class ASTNode {
    public abstract void accept(CFGVisitor visitor);
}
