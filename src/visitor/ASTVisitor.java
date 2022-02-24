package visitor;

import ast.AST;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.ArithExpr;
import ast.expr.ClassExpr;
import ast.expr.FieldExpr;
import ast.expr.IntExpr;
import ast.expr.MethodExpr;
import ast.expr.NullExpr;
import ast.expr.ThisExpr;
import ast.expr.VarExpr;
import ast.stmt.AssignStmt;
import ast.stmt.IfStmt;
import ast.stmt.PrintStmt;
import ast.stmt.ReturnStmt;
import ast.stmt.UpdateStmt;
import ast.stmt.WhileStmt;

public interface ASTVisitor {

    public void visit(AST node);

    public void visit(ClassDecl node);

    public void visit(MethodDecl node);

    public void visit(AssignStmt node);

    public void visit(IfStmt node);

    public void visit(PrintStmt node);

    public void visit(ReturnStmt node);

    public void visit(WhileStmt node);

    public void visit(UpdateStmt node);

    public void visit(IntExpr node);

    public void visit(VarExpr node);

    public void visit(MethodExpr node);

    public void visit(ThisExpr node);

    public void visit(FieldExpr node);

    public void visit(ClassExpr node);

    public void visit(ArithExpr node);

    public void visit(NullExpr node);
}
