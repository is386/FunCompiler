package ast;

import cfg.CFGVisitor;

public abstract class ASTNode {
    public abstract void accept(CFGVisitor visitor);
}
