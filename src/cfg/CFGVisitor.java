package cfg;

import java.util.*;

import ast.Program;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import cfg.primitives.*;
import cfg.stmt.*;

// TODO: Tag check before print
// TODO: Tag check before if/while conds
// TODO: SSA

public class CFGVisitor implements Visitor {
    private int tempVarCount = 1;
    private int blockCount = 1;
    private boolean badPtr = false;
    private boolean badNumber = false;
    private boolean badField = false;
    private boolean badMethod = false;
    private Stack<BasicBlock> currentBlock = new Stack<>();
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private Stack<Primitive> primitives = new Stack<>();
    private ArrayList<String> fields;
    private ArrayList<String> methodNames;
    private ArrayList<MethodDecl> methods = new ArrayList<>();
    private HashMap<String, Integer> fieldCounts = new HashMap<>();
    private Primitive assignedVar = null;
    private IntPrimitive addMask = new IntPrimitive("18446744073709551614");

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

        BasicBlock dataBlock = new BasicBlock("data");
        dataBlock.setAsHead();
        currentBlock.push(dataBlock);
        for (ClassDecl c : node.getClasses()) {
            c.accept(this);
        }
        blocks.add(currentBlock.pop());

        BasicBlock codeBlock = new BasicBlock("code");
        codeBlock.setAsHead();
        blocks.add(codeBlock);
        for (MethodDecl m : methods) {
            m.accept(this);
        }

        BasicBlock mainBlock = new BasicBlock("main");
        mainBlock.setAsHead();
        currentBlock.push(mainBlock);
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        if (currentBlock.peek().getControlStmt() == null) {
            currentBlock.peek().setControlStmt(new ControlReturn(new IntPrimitive(0, false)));
        }
        blocks.add(currentBlock.pop());
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
            currentBlock.peek().push(ir);
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
        arith.setOperand2(new IntPrimitive(8, false));

        if (caller.getType() == Type.UNDECLARED) {
            ArithPrimitive and = new ArithPrimitive("&");
            and.setOperand1(caller);
            and.setOperand2(new IntPrimitive(1, false));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, and);
            currentBlock.peek().push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.peek().setControlStmt(c);
            blocks.add(currentBlock.pop());
            currentBlock.push(new BasicBlock(ifBranchName));
            arith.setOperand1(tempVar);
        } else {
            arith.setOperand1(caller);
        }

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
        currentBlock.peek().push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.peek().push(ir);

        int offset = fields.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset, false);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.peek().push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        currentBlock.peek().setControlStmt(c);
        blocks.add(currentBlock.pop());
        currentBlock.push(new BasicBlock(ifBranchName));
        badField = true;

        SetEltPrimitive setElt = new SetEltPrimitive(caller, tempVar, newVal);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, setElt);
        currentBlock.peek().push(ir);
    }

    @Override
    public void visit(IfStmt node) {
        node.getCond().accept(this);
        Primitive condVar = primitives.pop();

        String ifBranchName = "l" + blockCount++;
        String elseBranchName = "l" + blockCount++;
        String jumpBlockName = "l" + blockCount;

        ControlStmt c;
        if (!node.getElseStatements().isEmpty()) {
            c = new ControlCond(condVar, ifBranchName, elseBranchName);
            blockCount++;
        } else {
            jumpBlockName = elseBranchName;
            c = new ControlCond(condVar, ifBranchName, jumpBlockName);
        }

        currentBlock.peek().setControlStmt(c);
        blocks.add(currentBlock.pop());

        BasicBlock nextBlock = new BasicBlock(jumpBlockName);
        currentBlock.push(nextBlock);

        currentBlock.push(new BasicBlock(ifBranchName));
        for (ASTStmt s : node.getIfStatements()) {
            s.accept(this);
        }
        BasicBlock top = currentBlock.pop();
        if (top.getControlStmt() == null) {
            top.setControlStmt(new ControlJump(jumpBlockName));
        }
        blocks.add(top);

        if (!node.getElseStatements().isEmpty()) {
            currentBlock.push(new BasicBlock(elseBranchName));
            for (ASTStmt s : node.getElseStatements()) {
                s.accept(this);
            }
            top = currentBlock.pop();
            if (top.getControlStmt() == null) {
                top.setControlStmt(new ControlJump(jumpBlockName));
            }
            blocks.add(top);
        }
    }

    @Override
    public void visit(WhileStmt node) {
        String loopName = "l" + blockCount++;
        ControlStmt c = new ControlJump(loopName);
        currentBlock.peek().setControlStmt(c);

        BasicBlock loopHead = new BasicBlock(loopName);
        node.getCond().accept(this);
        blocks.add(currentBlock.pop());

        Primitive condVar = primitives.pop();
        String bodyName = "l" + blockCount++;
        String finishName = "l" + blockCount++;
        c = new ControlCond(condVar, bodyName, finishName);
        loopHead.setControlStmt(c);
        blocks.add(loopHead);

        currentBlock.push(new BasicBlock(bodyName));
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }

        BasicBlock top = currentBlock.pop();
        if (top.getControlStmt() == null) {
            top.setControlStmt(new ControlJump(loopName));
        }
        blocks.add(top);
        currentBlock.push(new BasicBlock(finishName));
    }

    @Override
    public void visit(PrintStmt node) {
        node.getExpr().accept(this);
        PrintPrimitive p = new PrintPrimitive(primitives.pop());
        IRStmt ir = new IREqual(null, p);
        currentBlock.peek().push(ir);
    }

    @Override
    public void visit(ReturnStmt node) {
        node.getExpr().accept(this);
        ControlStmt c = new ControlReturn(primitives.pop());
        currentBlock.peek().setControlStmt(c);
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

        if (caller.getType() == Type.UNDECLARED) {
            ArithPrimitive arith = new ArithPrimitive("&");
            arith.setOperand1(caller);
            arith.setOperand2(new IntPrimitive(1, false));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, arith);
            currentBlock.peek().push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.peek().setControlStmt(c);
            blocks.add(currentBlock.pop());
            currentBlock.push(new BasicBlock(ifBranchName));
        }

        LoadPrimitive load = new LoadPrimitive(caller);

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.peek().push(ir);

        int offset = methodNames.indexOf(node.getName());
        offset = (offset == -1) ? 0 : offset;
        IntPrimitive offsetPrim = new IntPrimitive(offset, false);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offsetPrim);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.peek().push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badMethod");
        currentBlock.peek().setControlStmt(c);
        blocks.add(currentBlock.pop());
        currentBlock.push(new BasicBlock(ifBranchName));
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
        currentBlock.peek().push(ir);

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

        if (caller.getType() == Type.UNDECLARED) {
            arith = new ArithPrimitive("&");
            arith.setOperand1(caller);
            arith.setOperand2(new IntPrimitive(1, false));
            tempVar = getNextTemp();
            ir = new IREqual(tempVar, arith);
            currentBlock.peek().push(ir);
            ifBranchName = "l" + blockCount++;
            c = new ControlCond(tempVar, "badPtr", ifBranchName);
            badPtr = true;
            currentBlock.peek().setControlStmt(c);
            blocks.add(currentBlock.pop());
            currentBlock.push(new BasicBlock(ifBranchName));
        }

        arith = new ArithPrimitive("+");
        arith.setOperand1(caller);
        arith.setOperand2(new IntPrimitive(8, false));

        tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
        currentBlock.peek().push(ir);

        LoadPrimitive load = new LoadPrimitive(tempVar);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, load);
        currentBlock.peek().push(ir);

        IntPrimitive offset = new IntPrimitive(fields.indexOf(node.getName()), false);
        GetEltPrimitive getElt = new GetEltPrimitive(tempVar, offset);
        tempVar = getNextTemp();
        ir = new IREqual(tempVar, getElt);
        currentBlock.peek().push(ir);

        ifBranchName = "l" + blockCount++;
        c = new ControlCond(tempVar, ifBranchName, "badField");
        currentBlock.peek().setControlStmt(c);
        blocks.add(currentBlock.pop());
        currentBlock.push(new BasicBlock(ifBranchName));
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
        currentBlock.peek().push(ir);

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
        currentBlock.peek().push(ir);

        GlobalPrimitive global = new GlobalPrimitive("vtbl" + node.getName());
        StorePrimitive store = new StorePrimitive(returnVar, global);
        ir = new IREqual(null, store);
        currentBlock.peek().push(ir);

        ArithPrimitive arith = new ArithPrimitive("+");
        arith.setOperand1(returnVar);
        arith.setOperand2(new IntPrimitive(8, false));

        TempPrimitive tempVar = getNextTemp();
        ir = new IREqual(tempVar, arith);
        currentBlock.peek().push(ir);

        global = new GlobalPrimitive("fields" + node.getName());
        store = new StorePrimitive(tempVar, global);
        ir = new IREqual(null, store);
        currentBlock.peek().push(ir);
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
        if (operand1.getType() == Type.UNDECLARED) {
            checkIfNumber(operand1);
        }
        node.getExpr2().accept(this);
        Primitive operand2 = primitives.pop();
        if (operand2.getType() == Type.UNDECLARED) {
            checkIfNumber(operand2);
        }

        arith.setOperand1(operand1);
        arith.setOperand2(operand2);

        Primitive tempVar;
        Primitive tempVar2;
        ArithPrimitive arith2;
        IRStmt ir;
        switch (node.getOp()) {
            case "+":
                tempVar = getNextTemp();
                arith2 = new ArithPrimitive("&");
                arith2.setOperand1(operand1);
                arith2.setOperand2(addMask);
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);
                arith.setOperand1(tempVar);
                break;
            case "-":
                tempVar = getNextTemp();
                ir = new IREqual(tempVar, arith);
                currentBlock.peek().push(ir);
                arith2 = new ArithPrimitive("+");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(new IntPrimitive(1, false));
                returnVal = arith2;
                break;
            case "*":
                tempVar = getNextTemp();
                arith2 = new ArithPrimitive("/");
                arith2.setOperand1(operand1);
                arith2.setOperand2(new IntPrimitive(2, false));
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                tempVar2 = getNextTemp();
                arith2 = new ArithPrimitive("/");
                arith2.setOperand1(operand2);
                arith2.setOperand2(new IntPrimitive(2, false));
                ir = new IREqual(tempVar2, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("*");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(tempVar2);
                tempVar = getNextTemp();
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("*");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(new IntPrimitive(2, false));
                tempVar = getNextTemp();
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("+");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(new IntPrimitive(1, false));
                returnVal = arith2;
                break;
            case "/":
                tempVar = getNextTemp();
                arith2 = new ArithPrimitive("/");
                arith2.setOperand1(operand1);
                arith2.setOperand2(new IntPrimitive(2, false));
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                tempVar2 = getNextTemp();
                arith2 = new ArithPrimitive("/");
                arith2.setOperand1(operand2);
                arith2.setOperand2(new IntPrimitive(2, false));
                ir = new IREqual(tempVar2, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("/");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(tempVar2);
                tempVar = getNextTemp();
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("*");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(new IntPrimitive(2, false));
                tempVar = getNextTemp();
                ir = new IREqual(tempVar, arith2);
                currentBlock.peek().push(ir);

                arith2 = new ArithPrimitive("+");
                arith2.setOperand1(tempVar);
                arith2.setOperand2(new IntPrimitive(1, false));
                returnVal = arith2;
                break;
        }

        if (returnVal == null) {
            returnVal = arith;
        }

        if (returnVar == null && primitives.size() == 0) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.peek() instanceof VarPrimitive) {
            returnVar = getNextTemp();
        } else if (returnVar == null && primitives.size() != 0) {
            returnVar = primitives.pop();
        }
        ir = new IREqual(returnVar, returnVal);
        currentBlock.peek().push(ir);
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
        currentBlock.peek().push(irVtable);
        currentBlock.peek().push(irFields);
    }

    @Override
    public void visit(MethodDecl node) {
        BasicBlock methodBlock = new BasicBlock(node.getBlockName());
        methodBlock.setAsHead();
        currentBlock.push(methodBlock);
        for (ASTStmt s : node.getStatements()) {
            s.accept(this);
        }
        blocks.add(currentBlock.pop());
        tempVarCount = 1;
    }

    @Override
    public void visit(IntExpr node) {
        primitives.push(new IntPrimitive(node.getValue(), true));
    }

    @Override
    public void visit(VarExpr node) {
        primitives.push(new VarPrimitive(node.getName()));
    }

    @Override
    public void visit(ThisExpr node) {
        primitives.push(new ThisPrimitive());
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    private TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }

    private void checkIfNumber(Primitive prim) {
        ArithPrimitive arith = new ArithPrimitive("&");
        arith.setOperand1(prim);
        arith.setOperand2(new IntPrimitive(1, false));
        Primitive tempVar = getNextTemp();
        IREqual ir = new IREqual(tempVar, arith);
        currentBlock.peek().push(ir);
        String ifBranchName = "l" + blockCount++;
        ControlStmt c = new ControlCond(tempVar, ifBranchName, "badNumber");
        badNumber = true;
        currentBlock.peek().setControlStmt(c);
        blocks.add(currentBlock.pop());
        currentBlock.push(new BasicBlock(ifBranchName));
    }
}
