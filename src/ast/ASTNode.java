package ast;

import visitor.ASTVisitor;

public abstract class ASTNode {
    public abstract void accept(ASTVisitor visitor);
}
