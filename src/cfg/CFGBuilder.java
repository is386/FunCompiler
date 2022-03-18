package cfg;

import java.util.*;

import ast.AST;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import cfg.primitives.*;
import cfg.stmt.*;
import types.TypeChecker;
import visitor.ASTVisitor;

public class CFGBuilder implements ASTVisitor {
    private boolean notAPointer = false;
    private String currClass = "";
    private String currMethod = "";
    private int blockCount = 1;
    private CFG cfg = new CFG();
    private Stack<BasicBlock> blocks = new Stack<>();
    private Stack<Primitive> primitives = new Stack<>();
    private ArrayList<String> methodNames;
    private ArrayList<MethodDecl> methods = new ArrayList<>();
    private HashMap<String, Integer> fieldCounts = new HashMap<>();
    private HashMap<Primitive, String> primitiveTypes = new HashMap<>();
    private HashMap<String, ClassDecl> classes = new HashMap<>();
    private TypeChecker typeChecker;
    private Primitive assignedVar = null;

    public CFGBuilder(TypeChecker typeChecker) {
        this.typeChecker = typeChecker;
    }

    public CFG build(AST ast) {
        visit(ast);
        connectBlocks();
        addFailBlocks();
        cfg.resetVars();
        cfg.resetFuncBlocks();
        cfg.incrNumFuncs();
        return cfg;
    }

    private void addFailBlocks() {
        BasicBlock fail;
        if (notAPointer) {
            fail = new BasicBlock("isNullPointer", 0);
            fail.push(new IRFail("NotAPointer"));
            fail.setFail();
            cfg.add(fail);
        }
    }

    @Override
    public void visit(AST node) {
        LinkedHashSet<String> uniqueNames = new LinkedHashSet<>();

        for (ClassDecl c : node.getClasses()) {
            fieldCounts.put(c.getName(), c.getFields().size());
            for (TypedVarExpr tv : c.getFields()) {
                uniqueNames.add(tv.getName());
            }
            methods.addAll(c.getMethods());
        }
        uniqueNames.clear();

        for (MethodDecl m : methods) {
            uniqueNames.add(m.getName());
        }
        methodNames = new ArrayList<>(uniqueNames);

        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }

        for (MethodDecl m : methods) {
            currMethod = m.getName();
            currClass = m.getClassName();
            m.accept(this);
        }

        currMethod = "main";
        currClass = "main";
        BasicBlock mainBlock = new BasicBlock("main", 0);
        mainBlock.setAsHead();

        for (TypedVarExpr v : node.getLocalVars()) {
            cfg.addVar(v.getName());
            VarPrimitive vp = new VarPrimitive(v.getName());
            IREqual ir = new IREqual(vp, new IntPrimitive(0));
            mainBlock.push(ir);
        }

        blocks.push(mainBlock);
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        if (blocks.peek().getControlStmt() == null) {
            blocks.peek().setControlStmt(new ControlReturn(new IntPrimitive(0)));
        }
        cfg.add(blocks.pop());
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
            blocks.peek().push(ir);
        }
        assignedVar = null;
    }

    @Override
    public void visit(UpdateStmt node) {
        ArithPrimitive arith;
        TempPrimitive tempVar;
        IRStmt ir;

        node.getNewVal().accept(this);
        Primitive newVal = primitives.pop();
        node.getCaller().accept(this);
        Primitive caller = primitives.pop();

        String ifBranchName = "l" + blockCount++;
        ControlStmt c = new ControlCond(caller, ifBranchName, "isNullPointer");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        notAPointer = true;

        String type = primitiveTypes.get(caller);
        int offset = typeChecker.getFieldOffset(type, node.getName());
        arith = new ArithPrimitive("+");
        arith.setOperand2(new IntPrimitive(8 * offset));
        arith.setOperand1(caller);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, arith);
        blocks.peek().push(ir);

        SetEltPrimitive setElt = new SetEltPrimitive(tempVar, new IntPrimitive(0), newVal);
        ir = new IREqual(null, setElt);
        blocks.peek().push(ir);
    }

    @Override
    public void visit(IfStmt node) {
        node.getCond().accept(this);
        Primitive condVar = primitives.pop();
        int ifBlockNum = blockCount++;
        int elseBlockNum = blockCount++;
        int jumpBlockNum = blockCount;
        String ifBranchName = "l" + ifBlockNum;
        String elseBranchName = "l" + elseBlockNum;
        String jumpBlockName = "l" + jumpBlockNum;

        ControlStmt c;
        if (!node.getElseStatements().isEmpty()) {
            c = new ControlCond(condVar, ifBranchName, elseBranchName);
            blockCount++;
        } else {
            jumpBlockName = elseBranchName;
            jumpBlockNum = elseBlockNum;
            c = new ControlCond(condVar, ifBranchName, jumpBlockName);
        }

        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());

        BasicBlock nextBlock = new BasicBlock(jumpBlockName, jumpBlockNum);
        blocks.push(nextBlock);

        blocks.push(new BasicBlock(ifBranchName, ifBlockNum));
        for (ASTStmt s : node.getIfStatements()) {
            s.accept(this);
        }
        BasicBlock top = blocks.pop();
        if (top.getControlStmt() == null) {
            top.setControlStmt(new ControlJump(jumpBlockName));
        }
        cfg.add(top);

        if (!node.getElseStatements().isEmpty()) {
            blocks.push(new BasicBlock(elseBranchName, elseBlockNum));
            for (ASTStmt s : node.getElseStatements()) {
                s.accept(this);
            }
            top = blocks.pop();
            if (top.getControlStmt() == null) {
                top.setControlStmt(new ControlJump(jumpBlockName));
            }
            cfg.add(top);
        }
    }

    @Override
    public void visit(WhileStmt node) {
        node.getCond().accept(this);
        Primitive condVar = primitives.pop();
        String loopName = "l" + blockCount++;
        String jumpBack;
        BasicBlock loopHead = new BasicBlock(loopName, blockCount - 1);
        ControlStmt c;

        c = new ControlJump(loopName);
        blocks.peek().setControlStmt(c);
        jumpBack = loopName;
        cfg.add(blocks.pop());

        int bodyNum = blockCount++;
        int finishNum = blockCount++;
        String bodyName = "l" + bodyNum;
        String finishName = "l" + finishNum;
        c = new ControlCond(condVar, bodyName, finishName);
        loopHead.setControlStmt(c);
        cfg.add(loopHead);

        blocks.push(new BasicBlock(bodyName, bodyNum));
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        BasicBlock top = blocks.pop();
        if (top.getControlStmt() == null) {
            top.setControlStmt(new ControlJump(jumpBack));
        }
        cfg.add(top);
        blocks.push(new BasicBlock(finishName, finishNum));
    }

    @Override
    public void visit(PrintStmt node) {
        node.getExpr().accept(this);
        Primitive varToPrint = primitives.pop();
        PrintPrimitive p = new PrintPrimitive(varToPrint);
        IREqual ir = new IREqual(null, p);
        blocks.peek().push(ir);
    }

    @Override
    public void visit(ReturnStmt node) {
        node.getExpr().accept(this);
        ControlStmt c = new ControlReturn(primitives.pop());
        blocks.peek().setControlStmt(c);
    }

    @Override
    public void visit(MethodExpr node) {
        Primitive returnVar = null;
        TempPrimitive tempVar;
        IREqual ir;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getCaller().accept(this);
        Primitive caller = primitives.pop();

        String ifBranchName = "l" + blockCount++;
        ControlStmt c = new ControlCond(caller, ifBranchName, "isNullPointer");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        notAPointer = true;

        LoadPrimitive load = new LoadPrimitive(caller);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, load);
        blocks.peek().push(ir);

        int offset = methodNames.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, getElt);
        blocks.peek().push(ir);

        CallPrimitive call = new CallPrimitive(tempVar, caller);
        for (ASTExpr e : node.getArgs()) {
            e.accept(this);
            call.addArg(primitives.pop());
        }

        if (returnVar == null && primitives.size() == 0) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        ir = new IREqual(returnVar, call);
        blocks.peek().push(ir);

        String callerType = primitiveTypes.get(caller);
        String methodType = typeChecker.getTypeOfMethod(callerType, node.getName());
        primitiveTypes.put(returnVar, methodType);
        primitives.push(returnVar);
    }

    @Override
    public void visit(FieldExpr node) {
        Primitive returnVar = null;
        IRStmt ir;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getCaller().accept(this);
        Primitive caller = primitives.pop();

        String ifBranchName = "l" + blockCount++;
        ControlStmt c = new ControlCond(caller, ifBranchName, "isNullPointer");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        notAPointer = true;

        if (returnVar == null && primitives.size() == 0) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }

        String type = primitiveTypes.get(caller);
        int offset = typeChecker.getFieldOffset(type, node.getName());
        GetEltPrimitive getElt = new GetEltPrimitive(caller, new IntPrimitive(offset));
        ir = new IREqual(returnVar, getElt);
        blocks.peek().push(ir);

        String methodType = typeChecker.getTypeOfField(type, node.getName());
        primitiveTypes.put(returnVar, methodType);

        primitives.push(returnVar);
    }

    @Override
    public void visit(ClassExpr node) {
        int numFields = fieldCounts.get(node.getName());
        AllocPrimitive alloc = new AllocPrimitive(numFields + 4);

        // Compute GC Map
        long gcMap = 0;
        ArrayList<TypedVarExpr> fields = classes.get(node.getName()).getFields();

        for (TypedVarExpr f : fields) {
            int offset = typeChecker.getFieldOffset(node.getName(), f.getName());
            if (!f.getType().equals("int")) {
                gcMap |= (1 << offset);
            }
        }

        Primitive returnVar = null;
        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        } else {
            returnVar = cfg.getNextTemp();
        }
        IRStmt ir = new IREqual(returnVar, alloc);
        blocks.peek().push(ir);

        GlobalPrimitive global = new GlobalPrimitive("vtbl" + node.getName());
        StorePrimitive store = new StorePrimitive(returnVar, global);
        ir = new IREqual(null, store);
        blocks.peek().push(ir);

        // Get address of slot -1
        TempPrimitive temp = cfg.getNextTemp();
        ArithPrimitive arith = new ArithPrimitive("-");
        arith.setOperand2(new IntPrimitive(8));
        arith.setOperand1(returnVar);
        ir = new IREqual(temp, arith);
        blocks.peek().push(ir);

        // Store GC Map at slot -1
        store = new StorePrimitive(temp, new IntPrimitive(gcMap));
        ir = new IREqual(null, store);
        blocks.peek().push(ir);

        primitiveTypes.put(returnVar, node.getName());
        primitives.push(returnVar);
    }

    @Override
    public void visit(ArithExpr node) {
        ArithPrimitive arith = new ArithPrimitive(node.getOp());
        Primitive returnVar = null;
        Primitive returnVal = null;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getExpr1().accept(this);
        Primitive operand1 = primitives.pop();

        node.getExpr2().accept(this);
        Primitive operand2 = primitives.pop();

        arith.setOperand1(operand1);
        arith.setOperand2(operand2);

        if (returnVal == null) {
            returnVal = arith;
        }

        if (returnVar == null && primitives.size() == 0) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        IREqual ir = new IREqual(returnVar, returnVal);
        blocks.peek().push(ir);

        primitiveTypes.put(returnVar, "int");

        primitives.push(returnVar);
    }

    @Override
    public void visit(ClassDecl node) {
        ArrayList<Object> vtable = new ArrayList<>();
        for (String m : methodNames) {
            MethodDecl methodDecl = node.getMethodByName(m);
            if (methodDecl != null) {
                vtable.add(methodDecl.getName() + methodDecl.getClassName());
            } else {
                vtable.add(0);
            }
        }
        classes.put(node.getName(), node);
        IRData irVtable = new IRData("vtbl" + node.getName(), vtable);
        cfg.addToDataBlock(irVtable);
    }

    @Override
    public void visit(MethodDecl node) {
        BasicBlock methodBlock = new BasicBlock(node.getBlockName(), 0);

        for (TypedVarExpr t : node.getArgs()) {
            methodBlock.addParam(t.getName());
        }
        methodBlock.setAsHead();

        for (TypedVarExpr v : node.getLocalVars()) {
            cfg.addVar(v.getName());
            VarPrimitive vp = new VarPrimitive(v.getName());
            IREqual ir = new IREqual(vp, new IntPrimitive(0));
            methodBlock.push(ir);
        }
        blocks.push(methodBlock);

        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        if (blocks.peek().getControlStmt() == null) {
            blocks.peek().setControlStmt(new ControlReturn(new IntPrimitive(0)));
        }

        cfg.add(blocks.pop());
        cfg.resetTempVarCount();
        cfg.resetVars();
        cfg.resetFuncBlocks();
        cfg.incrNumFuncs();
    }

    @Override
    public void visit(IntExpr node) {
        IntPrimitive i = new IntPrimitive(node.getValue());
        primitiveTypes.put(i, "int");
        primitives.push(i);
    }

    @Override
    public void visit(VarExpr node) {
        VarPrimitive v = new VarPrimitive(node.getName());
        String type = typeChecker.getTypeOfVar(currClass, currMethod, v.getName());
        primitiveTypes.put(v, type);
        primitives.push(v);
        cfg.addVar(node.getName());
    }

    @Override
    public void visit(ThisExpr node) {
        ThisPrimitive t = new ThisPrimitive();
        primitiveTypes.put(t, currClass);
        primitives.push(t);
    }

    private void connectBlocks() {
        for (BasicBlock parent : cfg.getBlocks()) {
            ControlStmt c = parent.getControlStmt();
            if (c != null) {
                ArrayList<String> children = c.getBranchNames();
                for (BasicBlock child : cfg.getBlocks()) {
                    if (children.contains(child.getName())) {
                        parent.addChild(child);
                        child.addParent(parent);
                    }
                }
            }
        }
        removeUnreachable();
    }

    private void removeUnreachable() {
        HashSet<BasicBlock> unreachable = new HashSet<>();
        for (BasicBlock b : cfg.getBlocks()) {
            if (b.unreachable()) {
                unreachable.add(b);
            }
        }
        for (BasicBlock b : unreachable) {
            cfg.getBlocks().remove(b);
        }
    }

    @Override
    public void visit(NullExpr node) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TypedVarExpr node) {
        // TODO Auto-generated method stub

    }
}
