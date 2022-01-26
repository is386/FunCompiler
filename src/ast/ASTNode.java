package ast;

import cfg.CFGBuilder;

public abstract class ASTNode {
    public abstract void accept(CFGBuilder visitor);
}
