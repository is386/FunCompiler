package parse;

import expr.ASTExpr;

public class ParsePair {
    private ASTExpr node;
    private String line;

    public ParsePair(ASTExpr node, String restOfLine) {
        this.node = node;
        this.line = restOfLine;
    }

    public ASTExpr getNode() {
        return node;
    }

    public String getLine() {
        return line;
    }
}
