public class Response {
    private ASTExpr node;
    private String line;

    public Response(ASTExpr node, String restOfLine) {
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
