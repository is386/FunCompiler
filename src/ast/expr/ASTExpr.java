package ast.expr;

import ast.ASTNode;

public abstract class ASTExpr extends ASTNode {

    public abstract String toString();

    public String getName() {
        return "";
    }

    public Type getType() {
        return Type.UNDECLARED;
    }
}