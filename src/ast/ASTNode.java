package ast;

import cfg.CFGTransformer;

public abstract class ASTNode {
    public abstract void accept(CFGTransformer visitor);
}
