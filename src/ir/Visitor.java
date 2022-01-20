package ir;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;

public interface Visitor {

    public void visit(Program node);

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
}
