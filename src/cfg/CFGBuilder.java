package cfg;

import java.util.*;

import ast.AST;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import cfg.primitives.*;
import cfg.stmt.*;
import visitor.ASTVisitor;

public class CFGBuilder implements ASTVisitor {
    private int blockCount = 1;
    private boolean badField = false;
    private boolean badMethod = false;
    private CFG cfg = new CFG();
    private Stack<BasicBlock> blocks = new Stack<>();
    private Stack<Primitive> primitives = new Stack<>();
    private ArrayList<String> fields;
    private ArrayList<String> methodNames;
    private ArrayList<MethodDecl> methods = new ArrayList<>();
    private HashMap<String, Integer> fieldCounts = new HashMap<>();
    private Primitive assignedVar = null;

    public CFG build(AST ast) {
        visit(ast);
        addFailBlocks();
        connectBlocks();
        cfg.resetVars();
        cfg.resetFuncBlocks();
        cfg.incrNumFuncs();
        return cfg;
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
        fields = new ArrayList<>(uniqueNames);
        uniqueNames.clear();

        for (MethodDecl m : methods) {
            uniqueNames.add(m.getName());
        }
        methodNames = new ArrayList<>(uniqueNames);

        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }

        for (MethodDecl m : methods) {
            m.accept(this);
        }

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

    private void addFailBlocks() {
        BasicBlock fail;
        if (badField) {
            fail = new BasicBlock("badField", 0);
            fail.push(new IRFail("NoSuchField"));
            fail.setFail();
            cfg.add(fail);
        }

        if (badMethod) {
            fail = new BasicBlock("badMethod", 0);
            fail.push(new IRFail("NoSuchMethod"));
            fail.setFail();
            cfg.add(fail);
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
            blocks.peek().push(ir);
        }
        assignedVar = null;
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
        arith.setOperand2(new IntPrimitive(8));
        arith.setOperand1(caller);

        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, arith);
        blocks.peek().push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, load);
        blocks.peek().push(ir);

        int offset = fields.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, getElt);
        blocks.peek().push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        badField = true;

        SetEltPrimitive setElt = new SetEltPrimitive(caller, tempVar, newVal);
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
        String ifBranchName;
        ControlStmt c;

        if (assignedVar != null) {
            returnVar = assignedVar;
            assignedVar = null;
        }

        node.getCaller().accept(this);
        Primitive caller = primitives.pop();
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

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badMethod");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        badMethod = true;

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

        primitives.push(returnVar);
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

        arith = new ArithPrimitive("+");
        arith.setOperand1(caller);
        arith.setOperand2(new IntPrimitive(8));

        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, arith);
        blocks.peek().push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, load);
        blocks.peek().push(ir);

        IntPrimitive offset = new IntPrimitive(fields.indexOf(node.getName()));
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offset);
        tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, getElt);
        blocks.peek().push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        blocks.peek().setControlStmt(c);
        cfg.add(blocks.pop());
        blocks.push(new BasicBlock(ifBranchName, blockCount - 1));
        badField = true;

        if (returnVar == null && primitives.size() == 0) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = cfg.getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        getElt = new GetEltPrimitive(caller, tempVar);
        ir = new IREqual(returnVar, getElt);
        blocks.peek().push(ir);

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
            returnVar = cfg.getNextTemp();
        }
        IRStmt ir = new IREqual(returnVar, alloc);
        blocks.peek().push(ir);

        GlobalPrimitive global = new GlobalPrimitive("vtbl" + node.getName());
        StorePrimitive store = new StorePrimitive(returnVar, global);
        ir = new IREqual(null, store);
        blocks.peek().push(ir);

        ArithPrimitive arith = new ArithPrimitive("+");
        arith.setOperand1(returnVar);
        arith.setOperand2(new IntPrimitive(8));

        TempPrimitive tempVar = cfg.getNextTemp();
        ir = new IREqual(tempVar, arith);
        blocks.peek().push(ir);

        global = new GlobalPrimitive("fields" + node.getName());
        store = new StorePrimitive(tempVar, global);
        ir = new IREqual(null, store);
        blocks.peek().push(ir);
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
        primitives.push(returnVar);
    }

    @Override
    public void visit(ClassDecl node) {
        int offset = 2;
        ArrayList<Object> fieldMap = new ArrayList<>();
        for (String f : fields) {
            boolean add = false;
            for (TypedVarExpr field : node.getFields()) {
                if (field.getName().equals(f)) {
                    add = true;
                    break;
                }
            }
            if (add) {
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
        cfg.addToDataBlock(irVtable);
        cfg.addToDataBlock(irFields);
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
        primitives.push(new IntPrimitive(node.getValue()));
    }

    @Override
    public void visit(VarExpr node) {
        primitives.push(new VarPrimitive(node.getName()));
        cfg.addVar(node.getName());
    }

    @Override
    public void visit(ThisExpr node) {
        ThisPrimitive t = new ThisPrimitive();
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
