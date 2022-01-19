package ir;

import java.util.ArrayList;
import java.util.Stack;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.ArithExpr;
import ast.expr.ClassExpr;
import ast.expr.FieldExpr;
import ast.expr.IntExpr;
import ast.expr.MethodExpr;
import ast.expr.ThisExpr;
import ast.expr.VarExpr;
import ast.stmt.ASTStmt;
import ast.stmt.AssignStmt;
import ast.stmt.IfStmt;
import ast.stmt.PrintStmt;
import ast.stmt.ReturnStmt;
import ast.stmt.UpdateStmt;
import ast.stmt.WhileStmt;
import ir.primitives.ArithPrimitive;
import ir.primitives.IntPrimitive;
import ir.primitives.Primitive;
import ir.primitives.PrintPrimitive;
import ir.primitives.TempPrimitive;
import ir.primitives.VarPrimitive;

public class CFGVisitor implements Visitor {

    private int tempVarCount = 1;
    private BasicBlock currentBlock;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private Stack<Primitive> primitives = new Stack<>();

    @Override
    public void visit(Program node) {
        currentBlock = new BasicBlock("main");
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }
        System.out.println(currentBlock);
    }

    @Override
    public void visit(AssignStmt node) {
        node.getExpr().accept(this);
        Primitive var = new VarPrimitive(node.getVar());
        Primitive p = primitives.pop();
        IRStmt ir;
        if (p instanceof TempPrimitive) {
            ir = currentBlock.pop();
            ir.setVar(var);
        } else {
            ir = new IRStmt(var, p);
        }
        currentBlock.push(ir);
    }

    @Override
    public void visit(IfStmt node) {
    }

    @Override
    public void visit(PrintStmt node) {
        node.getExpr().accept(this);
        PrintPrimitive p = new PrintPrimitive(primitives.pop());
        IRStmt ir = new IRStmt(null, p);
        currentBlock.push(ir);
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
    public void visit(IntExpr node) {
        primitives.push(new IntPrimitive(node.getValue()));
    }

    @Override
    public void visit(VarExpr node) {
        primitives.push(new VarPrimitive(node.getName()));
    }

    @Override
    public void visit(MethodExpr node) {
    }

    @Override
    public void visit(ThisExpr node) {
        primitives.push(new VarPrimitive("this"));
    }

    @Override
    public void visit(FieldExpr node) {
    }

    @Override
    public void visit(ClassExpr node) {
    }

    @Override
    public void visit(ArithExpr node) {
        ArithPrimitive arithPrimitive = new ArithPrimitive(node.getOp());
        node.getExpr1().accept(this);
        arithPrimitive.setPrim1(primitives.pop());
        node.getExpr2().accept(this);
        arithPrimitive.setPrim2(primitives.pop());
        String varNum = Integer.toString(tempVarCount);
        tempVarCount++;
        TempPrimitive tempVar = new TempPrimitive(varNum);
        IRStmt ir = new IRStmt(tempVar, arithPrimitive);
        currentBlock.push(ir);
        primitives.push(tempVar);
    }

    @Override
    public void visit(ClassDecl node) {
    }

    @Override
    public void visit(MethodDecl node) {
    }
}
