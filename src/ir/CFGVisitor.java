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

public class CFGVisitor implements Visitor {

    @Override
    public void visit(EqualStmt node) {

    }

    @Override
    public void visit(IfStmt node) {

    }

    @Override
    public void visit(PrintStmt node) {

    }

    @Override
    public void visit(ReturnStmt node) {

    }

    @Override
    public void visit(WhileStmt node) {

    }

    @Override
    public void visit(UpdateStmt node) {

    }

    @Override
    public void visit(IntegerExpr node) {

    }

    @Override
    public void visit(VariableExpr node) {

    }

    @Override
    public void visit(MethodExpr node) {

    }

    @Override
    public void visit(ThisExpr node) {

    }

    @Override
    public void visit(FieldExpr node) {

    }

    @Override
    public void visit(ClassExpr node) {

    }

    @Override
    public void visit(ArithExpr node) {

    }

    @Override
    public void visit(Program node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ClassDecl node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MethodDecl node) {
        // TODO Auto-generated method stub

    }
}
