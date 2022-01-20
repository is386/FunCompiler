package ir;

import java.util.*;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import ir.primitives.*;
import ir.stmt.IRData;
import ir.stmt.IREqual;
import ir.stmt.IRStmt;

public class CFGVisitor implements Visitor {

    private int tempVarCount = 1;
    private BasicBlock currentBlock;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private Stack<Primitive> primitives = new Stack<>();
    private ArrayList<String> fields;
    private ArrayList<String> methodNames;
    private ArrayList<MethodDecl> methods = new ArrayList<>();
    private HashMap<String, Integer> fieldCounts = new HashMap<>();
    private Primitive assignedVar = null;

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    @Override
    public void visit(Program node) {
        LinkedHashSet<String> uniqueNames = new LinkedHashSet<>();

        for (ClassDecl c : node.getClasses()) {
            fieldCounts.put(c.getName(), c.getFields().size());
            uniqueNames.addAll(c.getFields());
            methods.addAll(c.getMethods());
        }
        fields = new ArrayList<>(uniqueNames);
        uniqueNames.clear();

        currentBlock = new BasicBlock("data");
        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }
        blocks.add(currentBlock);

        blocks.add(new BasicBlock("code"));
        for (MethodDecl m : methods) {
            uniqueNames.add(m.getName());
            m.accept(this);
        }
        methodNames = new ArrayList<>(uniqueNames);

        currentBlock = new BasicBlock("main");
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }
        blocks.add(currentBlock);
    }

    @Override
    public void visit(AssignStmt node) {
        Primitive var;
        if (node.getVar() == "_") {
            var = null;
        } else {
            var = new VarPrimitive(node.getVar());
        }
        assignedVar = var;
        node.getExpr().accept(this);

        IREqual ir;
        if (assignedVar != null) {
            Primitive p = primitives.pop();
            ir = new IREqual(var, p);
            currentBlock.push(ir);
        }
        assignedVar = null;
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
        Primitive returnVar = null;
        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

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

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        getElt = new GetEltPrimitive(caller, tempVar);
        ir = new IREqual(returnVar, getElt);
        currentBlock.push(ir);

        primitives.push(returnVar);
    }

    @Override
    public void visit(ClassExpr node) {
        int allocSpace = fieldCounts.get(node.getName());
        AllocPrimitive alloc = new AllocPrimitive(allocSpace + 2);

        Primitive returnVar = null;
        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        } else {
            returnVar = getNextTemp();
        }
        IRStmt ir = new IREqual(returnVar, alloc);
        currentBlock.push(ir);

        GlobalPrimitive global = new GlobalPrimitive("vtbl" + node.getName());
        StorePrimitive store = new StorePrimitive(returnVar, global);
        ir = new IREqual(null, store);
        currentBlock.push(ir);

        ArithPrimitive arithPrimitive = new ArithPrimitive("+");
        arithPrimitive.setPrim1(returnVar);
        arithPrimitive.setPrim2(new IntPrimitive(8));

        TempPrimitive tempVar = getNextTemp();
        ir = new IREqual(tempVar, arithPrimitive);
        currentBlock.push(ir);

        global = new GlobalPrimitive("fields" + node.getName());
        store = new StorePrimitive(tempVar, global);
        ir = new IREqual(null, store);
        currentBlock.push(ir);
        primitives.push(returnVar);
    }

    @Override
    public void visit(ArithExpr node) {
        ArithPrimitive arithPrimitive = new ArithPrimitive(node.getOp());
        Primitive returnVar = null;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getExpr1().accept(this);
        arithPrimitive.setPrim1(primitives.pop());

        node.getExpr2().accept(this);
        arithPrimitive.setPrim2(primitives.pop());

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }

        IRStmt ir = new IREqual(returnVar, arithPrimitive);
        currentBlock.push(ir);
        primitives.push(returnVar);
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
            vtable.add(m.getName() + m.getClassName());
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
