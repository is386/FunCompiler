package ir;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.ArithExpr;
import ast.expr.ClassExpr;
import ast.expr.FieldExpr;
import ast.expr.IntegerExpr;
import ast.expr.MethodExpr;
import ast.expr.ThisExpr;
import ast.expr.VariableExpr;
import ast.stmt.EqualStmt;
import ast.stmt.IfStmt;
import ast.stmt.PrintStmt;
import ast.stmt.ReturnStmt;
import ast.stmt.UpdateStmt;
import ast.stmt.WhileStmt;

public interface Visitor {

    public void visit(Program node);

    public void visit(ClassDecl node);

    public void visit(MethodDecl node);

    public void visit(EqualStmt node);

    public void visit(IfStmt node);

    public void visit(PrintStmt node);

    public void visit(ReturnStmt node);

    public void visit(WhileStmt node);

    public void visit(UpdateStmt node);

    public void visit(IntegerExpr node);

    public void visit(VariableExpr node);

    public void visit(MethodExpr node);

    public void visit(ThisExpr node);

    public void visit(FieldExpr node);

    public void visit(ClassExpr node);

    public void visit(ArithExpr node);
}
