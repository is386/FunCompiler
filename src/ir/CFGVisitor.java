package ir;

import java.util.*;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import ir.primitives.*;
import ir.stmt.*;

// TODO: Tagged Integers
// TODO: Use Stack for Current Block
// TODO: Basic Block Pointers
// TODO: If Statements
// TODO: While Statements

public class CFGVisitor implements Visitor {
    private int tempVarCount = 1;
    private int blockCount = 1;
    private boolean badPtr = false;
    private boolean badNumber = false;
    private boolean badField = false;
    private boolean badMethod = false;
    private BasicBlock currentBlock;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private LinkedList<BasicBlock> blockQueue = new LinkedList<>();
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

        for (MethodDecl m : methods) {
            uniqueNames.add(m.getName());
        }
        methodNames = new ArrayList<>(uniqueNames);

        currentBlock = new BasicBlock("data");
        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }
        blocks.add(currentBlock);

        blocks.add(new BasicBlock("code"));
        for (MethodDecl m : methods) {
            m.accept(this);
        }

        currentBlock = new BasicBlock("main");
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        if (currentBlock.getControlStmt() == null) {
            currentBlock.setControlStmt(new ControlReturn(new IntPrimitive(0)));
        }
        blocks.add(currentBlock);
        addFailBlocks();
    }

    private void addFailBlocks() {
        BasicBlock fail;

        if (badPtr) {
            fail = new BasicBlock("badPtr");
            fail.push(new IRFail("NotAPointer"));
            blocks.add(fail);
        }

        if (badNumber) {
            fail = new BasicBlock("badNumber");
            fail.push(new IRFail("NotANumber"));
            blocks.add(fail);
        }

        if (badField) {
            fail = new BasicBlock("badField");
            fail.push(new IRFail("NoSuchField"));
            blocks.add(fail);
        }

        if (badMethod) {
            fail = new BasicBlock("badMethod");
            fail.push(new IRFail("NoSuchMethod"));
            blocks.add(fail);
        }
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
        ControlStmt c = new ControlReturn(primitives.pop());
        currentBlock.setControlStmt(c);
    }

    @Override
    public void visit(WhileStmt node) {
    }

    @Override
    public void visit(UpdateStmt node) {
        ArithPrimitive arith;
        TempPrimitive tempVar;
        IRStmt ir;
        String ifBranchName;
        ControlStmt c;

        node.getNewVal().accept(this);
        Primitive newVal = primitives.pop();
        node.getCaller().accept(this);
        Primitive caller = primitives.pop();
        arith = new ArithPrimitive("+");
        arith.setPrim2(new IntPrimitive(8));

        if (caller.isVar()) {
            ArithPrimitive and = new ArithPrimitive("&");
            and.setPrim1(caller);
            and.setPrim2(new IntPrimitive(1));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, and);
            currentBlock.push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.setControlStmt(c);
            blocks.add(currentBlock);
            currentBlock = new BasicBlock(ifBranchName);
            arith.setPrim1(tempVar);
        } else {
            arith.setPrim1(caller);
        }

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
        currentBlock.push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.push(ir);

        int offset = fields.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        currentBlock.setControlStmt(c);
        blocks.add(currentBlock);
        currentBlock = new BasicBlock(ifBranchName);
        badField = true;

        SetEltPrimitive setElt = new SetEltPrimitive(caller, tempVar, newVal);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, setElt);
        currentBlock.push(ir);
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
        Primitive returnVar = null;
        TempPrimitive tempVar;
        IREqual ir;
        String ifBranchName;
        ControlStmt c;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getCaller().accept(this);
        Primitive caller = primitives.pop();

        if (caller.isVar()) {
            ArithPrimitive arith = new ArithPrimitive("&");
            arith.setPrim1(caller);
            arith.setPrim2(new IntPrimitive(1));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, arith);
            currentBlock.push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.setControlStmt(c);
            blocks.add(currentBlock);
            currentBlock = new BasicBlock(ifBranchName);
        }

        LoadPrimitive load = new LoadPrimitive(caller);

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.push(ir);

        int offset = methodNames.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badMethod");
        currentBlock.setControlStmt(c);
        blocks.add(currentBlock);
        currentBlock = new BasicBlock(ifBranchName);
        badMethod = true;

        CallPrimitive call = new CallPrimitive(tempVar, caller);
        for (ASTExpr e : node.getArgs()) {
            e.accept(this);
            call.addArg(primitives.pop());
        }

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        ir = new IREqual(returnVar, call);
        currentBlock.push(ir);

        primitives.push(returnVar);
    }

    @Override
    public void visit(ThisExpr node) {
        primitives.push(new VarPrimitive("this"));
    }

    @Override
    public void visit(FieldExpr node) {
        Primitive returnVar = null;
        ArithPrimitive arith;
        TempPrimitive tempVar;
        IRStmt ir;
        String ifBranchName;
        ControlStmt c;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getCaller().accept(this);
        Primitive caller = primitives.pop();

        if (caller.isVar()) {
            arith = new ArithPrimitive("&");
            arith.setPrim1(caller);
            arith.setPrim2(new IntPrimitive(1));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, arith);
            currentBlock.push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.setControlStmt(c);
            blocks.add(currentBlock);
            currentBlock = new BasicBlock(ifBranchName);
        }

        arith = new ArithPrimitive("+");
        arith.setPrim1(caller);
        arith.setPrim2(new IntPrimitive(8));

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
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

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        currentBlock.setControlStmt(c);
        blocks.add(currentBlock);
        currentBlock = new BasicBlock(ifBranchName);
        badField = true;

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
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

        ArithPrimitive arith = new ArithPrimitive("+");
        arith.setPrim1(returnVar);
        arith.setPrim2(new IntPrimitive(8));

        TempPrimitive tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
        currentBlock.push(ir);

        global = new GlobalPrimitive("fields" + node.getName());
        store = new StorePrimitive(tempVar, global);
        ir = new IREqual(null, store);
        currentBlock.push(ir);
        primitives.push(returnVar);
    }

    @Override
    public void visit(ArithExpr node) {
        ArithPrimitive arith = new ArithPrimitive(node.getOp());
        Primitive returnVar = null;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getExpr1().accept(this);
        arith.setPrim1(primitives.pop());

        node.getExpr2().accept(this);
        arith.setPrim2(primitives.pop());

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        IRStmt ir = new IREqual(returnVar, arith);
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
        for (String m : methodNames) {
            MethodDecl methodDecl = node.getMethodByName(m);
            if (methodDecl != null) {
                vtable.add(methodDecl.getName() + methodDecl.getClassName());
            } else {
                vtable.add(0);
            }
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
        tempVarCount = 1;
    }

    public TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }
}
