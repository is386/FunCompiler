package ir;

import java.util.ArrayList;
import java.util.HashMap;
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
import ir.primitives.AllocPrimitive;
import ir.primitives.ArithPrimitive;
import ir.primitives.GetEltPrimitive;
import ir.primitives.GlobalPrimitive;
import ir.primitives.IntPrimitive;
import ir.primitives.LoadPrimitive;
import ir.primitives.Primitive;
import ir.primitives.PrintPrimitive;
import ir.primitives.ReturnPrimitive;
import ir.primitives.StorePrimitive;
import ir.primitives.TempPrimitive;
import ir.primitives.VarPrimitive;

public class CFGVisitor implements Visitor {

    private int tempVarCount = 1;
    private BasicBlock currentBlock;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private Stack<Primitive> primitives = new Stack<>();
    private ArrayList<String> fields = new ArrayList<>();
    private ArrayList<MethodDecl> methods = new ArrayList<>();
    private HashMap<String, Integer> fieldCounts = new HashMap<>();

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    @Override
    public void visit(Program node) {
        for (ClassDecl c : node.getClasses()) {
            for (String f : c.getFields()) {
                if (!fields.contains(f)) {
                    fields.add(f);
                }
            }
            fieldCounts.put(c.getName(), c.getFields().size());
            methods.addAll(c.getMethods());
        }

        currentBlock = new BasicBlock("data");
        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }
        blocks.add(currentBlock);

        for (MethodDecl m : methods) {
            m.accept(this);
        }

        currentBlock = new BasicBlock("main");
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }
        blocks.add(currentBlock);
    }

    @Override
    public void visit(AssignStmt node) {
        node.getExpr().accept(this);

        Primitive var;
        if (node.getVar() == "_") {
            var = null;
        } else {
            var = new VarPrimitive(node.getVar());
        }

        IREqual ir;
        Primitive p = primitives.pop();
        ir = new IREqual(var, p);
        currentBlock.push(ir);
    }

    @Override
    public void visit(IfStmt node) {
    }

    @Override
    public void visit(PrintStmt node) {
        node.getExpr().accept(this);
        PrintPrimitive p = new PrintPrimitive(primitives.pop());
        IRStmt ir = new IREqual(null, p);
        currentBlock.push(ir);
    }

    @Override
    public void visit(ReturnStmt node) {
        node.getExpr().accept(this);
        ReturnPrimitive r = new ReturnPrimitive(primitives.pop());
        IRStmt ir = new IREqual(null, r);
        currentBlock.push(ir);
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
        node.getCaller().accept(this);
        ArithPrimitive arithPrimitive = new ArithPrimitive("+");
        Primitive caller = primitives.pop();
        arithPrimitive.setPrim1(caller);
        arithPrimitive.setPrim2(new IntPrimitive(8));

        TempPrimitive tempVar = getNextTemp();
        IREqual ir = new IREqual(tempVar, arithPrimitive);
        currentBlock.push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.push(ir);

        IntPrimitive offset = new IntPrimitive(fields.indexOf(node.getName()));
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offset);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.push(ir);

        getElt = new GetEltPrimitive(caller, tempVar);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.push(ir);

        primitives.push(tempVar);
    }

    @Override
    public void visit(ClassExpr node) {
        int allocSpace = fieldCounts.get(node.getName());
        AllocPrimitive alloc = new AllocPrimitive(allocSpace + 2);

        TempPrimitive tempVar1 = getNextTemp();
        IRStmt ir = new IREqual(tempVar1, alloc);
        currentBlock.push(ir);

        GlobalPrimitive global = new GlobalPrimitive("vtbl" + node.getName());
        StorePrimitive store = new StorePrimitive(tempVar1, global);
        ir = new IREqual(null, store);
        currentBlock.push(ir);

        ArithPrimitive arithPrimitive = new ArithPrimitive("+");
        arithPrimitive.setPrim1(tempVar1);
        arithPrimitive.setPrim2(new IntPrimitive(8));

        TempPrimitive tempVar2 = getNextTemp();
        ir = new IREqual(tempVar2, arithPrimitive);
        currentBlock.push(ir);

        global = new GlobalPrimitive("fields" + node.getName());
        store = new StorePrimitive(tempVar2, global);
        ir = new IREqual(null, store);
        currentBlock.push(ir);
        primitives.push(tempVar1);
    }

    @Override
    public void visit(ArithExpr node) {
        ArithPrimitive arithPrimitive = new ArithPrimitive(node.getOp());

        node.getExpr1().accept(this);
        arithPrimitive.setPrim1(primitives.pop());

        node.getExpr2().accept(this);
        arithPrimitive.setPrim2(primitives.pop());

        TempPrimitive tempVar = getNextTemp();
        IRStmt ir = new IREqual(tempVar, arithPrimitive);
        currentBlock.push(ir);
        primitives.push(tempVar);
    }

    @Override
    public void visit(ClassDecl node) {
        int offset = 2;
        ArrayList<Object> fieldMap = new ArrayList<>();
        for (String f : fields) {
            if (node.getFields().contains(f)) {
                fieldMap.add(offset++);
            } else {
                fieldMap.add(0);
            }
        }

        ArrayList<Object> vtable = new ArrayList<>();
        for (MethodDecl m : node.getMethods()) {
            vtable.add(m.getName());
        }

        IRData irVtable = new IRData("vtbl" + node.getName(), vtable);
        IRData irFields = new IRData("fields" + node.getName(), fieldMap);
        currentBlock.push(irVtable);
        currentBlock.push(irFields);
    }

    @Override
    public void visit(MethodDecl node) {
        currentBlock = new BasicBlock(node.getBlockName());
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }
        blocks.add(currentBlock);
    }

    public TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }
}
