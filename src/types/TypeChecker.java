package types;

import java.util.*;

import ast.AST;
import ast.decl.ClassDecl;
import ast.decl.MethodDecl;
import ast.expr.*;
import ast.stmt.*;
import visitor.ASTVisitor;

public class TypeChecker implements ASTVisitor {

    private HashSet<String> types = new HashSet<>();
    private HashSet<String> classes = new HashSet<>();
    private HashMap<String, HashMap<String, String>> methodReturnTypes = new HashMap<>();
    private HashMap<String, HashMap<String, ArrayList<String>>> methodArgTypes = new HashMap<>();
    private HashMap<String, HashMap<String, String>> fieldTypes = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> fieldOffsets = new HashMap<>();
    private HashMap<String, HashMap<String, HashMap<String, String>>> methodVarTypes = new HashMap<>();
    private HashMap<String, String> varTypes = new HashMap<>();
    private Stack<String> typeStack = new Stack<>();
    private String currentClass = "";
    private String currentMethod = "";

    public HashMap<String, HashMap<String, Integer>> getFieldOffsets() {
        return fieldOffsets;
    }

    public HashMap<String, HashMap<String, HashMap<String, String>>> getMethodVarTypes() {
        return methodVarTypes;
    }

    @Override
    public void visit(AST node) {
        types.add("int");
        for (ClassDecl c : node.getClasses()) {
            if (classes.contains(c.getName()) || types.contains(c.getName())) {
                showError(String.format("type '%s' defined twice", c.getName()));
            }
            classes.add(c.getName());
            types.add(c.getName());
        }

        for (ClassDecl c : node.getClasses()) {
            generateTypeEnv(c);
        }

        for (ClassDecl c : node.getClasses()) {
            currentClass = c.getName();
            c.accept(this);
        }

        currentClass = "";
        currentMethod = "main";
        varTypes = new HashMap<>();

        for (TypedVarExpr lv : node.getLocalVars()) {
            lv.accept(this);
        }

        for (ASTStmt as : node.getStatements()) {
            as.accept(this);
        }
        methodVarTypes.put("main", new HashMap<>());
        methodVarTypes.get("main").put("main", varTypes);
    }

    private void generateTypeEnv(ClassDecl node) {
        fieldTypes.put(node.getName(), new HashMap<>());
        fieldOffsets.put(node.getName(), new HashMap<>());
        int offset = 1;

        for (TypedVarExpr f : node.getFields()) {
            if (!types.contains(f.getType())) {
                nonExistentTypeError(f.getType());
            }
            fieldTypes.get(node.getName()).put(f.getName(), f.getType());
            fieldOffsets.get(node.getName()).put(f.getName(), offset++);
        }

        methodReturnTypes.put(node.getName(), new HashMap<>());
        methodArgTypes.put(node.getName(), new HashMap<>());
        for (MethodDecl m : node.getMethods()) {
            if (!types.contains(m.getReturnType())) {
                nonExistentTypeError(m.getReturnType());
            }
            methodReturnTypes.get(m.getClassName()).put(m.getName(), m.getReturnType());

            methodArgTypes.get(m.getClassName()).put(m.getName(), new ArrayList<>());
            for (TypedVarExpr a : m.getArgs()) {
                if (!types.contains(a.getType())) {
                    nonExistentTypeError(a.getType());
                }
                if (a.getType().equals(node.getName())) {
                    continue;
                }
                methodArgTypes.get(m.getClassName()).get(m.getName()).add(a.getType());
            }
        }
    }

    @Override
    public void visit(ClassDecl node) {
        methodVarTypes.put(node.getName(), new HashMap<>());
        for (MethodDecl m : node.getMethods()) {
            varTypes = new HashMap<>();
            m.accept(this);
        }
    }

    @Override
    public void visit(MethodDecl node) {
        currentMethod = node.getName();
        for (TypedVarExpr lv : node.getLocalVars()) {
            lv.accept(this);
        }
        for (TypedVarExpr a : node.getArgs()) {
            a.accept(this);
        }
        for (ASTStmt as : node.getStatements()) {
            as.accept(this);
        }
        methodVarTypes.get(node.getClassName()).put(currentMethod, varTypes);
    }

    @Override
    public void visit(AssignStmt node) {
        node.getExpr().accept(this);
        String exprType = typeStack.pop();

        if (node.getVar().equals("_")) {
            return;
        }

        String varType = varTypes.get(node.getVar());
        if (!varType.equals(exprType)) {
            showError(String.format("variable '%s' does not match type of expression", node.getVar()));
        }
    }

    @Override
    public void visit(IfStmt node) {
        node.getCond().accept(this);
        String condType = typeStack.pop();
        if (!condType.equals("int")) {
            showError("condition of IF must be integer");
        }

        for (ASTStmt as : node.getIfStatements()) {
            as.accept(this);
        }
        for (ASTStmt as : node.getElseStatements()) {
            as.accept(this);
        }
    }

    @Override
    public void visit(PrintStmt node) {
        node.getExpr().accept(this);
        String argType = typeStack.pop();
        if (!argType.equals("int")) {
            showError("cannot call print on non-integer expression");
        }
    }

    @Override
    public void visit(ReturnStmt node) {
        node.getExpr().accept(this);
        String exprType = typeStack.pop();

        if (currentClass.isEmpty() && currentMethod.equals("main")) {
            if (!exprType.equals("int")) {
                showError("return type of 'main' must be integer");
            }
            return;
        }

        String returnType = methodReturnTypes.get(currentClass).get(currentMethod);
        if (!exprType.equals(returnType)) {
            showError(String.format("returned expression in '%s' does not match return type '%s'", currentMethod,
                    returnType));
        }
    }

    @Override
    public void visit(WhileStmt node) {
        node.getCond().accept(this);
        String condType = typeStack.pop();
        if (!condType.equals("int")) {
            showError("condition of WHILE must be integer");
        }

        for (ASTStmt as : node.getStatements()) {
            as.accept(this);
        }
    }

    @Override
    public void visit(UpdateStmt node) {
        node.getCaller().accept(this);
        String callerType = typeStack.pop();
        if (!classes.contains(callerType)) {
            nonExistentTypeError(callerType);
        } else if (!fieldTypes.get(callerType).containsKey(node.getName())) {
            showError(String.format("type '%s' does not have field '%s'", callerType, node.getName()));
        }

        node.getNewVal().accept(this);
        String newValType = typeStack.pop();
        String fieldType = fieldTypes.get(callerType).get(node.getName());
        if (!fieldType.equals(newValType)) {
            showError(String.format("field '%s' does not match type of expression", node.getName()));
        }
    }

    @Override
    public void visit(IntExpr node) {
        typeStack.push("int");
    }

    @Override
    public void visit(VarExpr node) {
        if (!varTypes.containsKey(node.getName())) {
            showError(String.format("variable '%s' not initialized", node.getName()));
        }
        typeStack.push(varTypes.get(node.getName()));
    }

    @Override
    public void visit(TypedVarExpr node) {
        if (!types.contains(node.getType())) {
            nonExistentTypeError(node.getType());
        }
        varTypes.put(node.getName(), node.getType());
    }

    @Override
    public void visit(MethodExpr node) {
        node.getCaller().accept(this);
        String callerType = typeStack.pop();
        if (!classes.contains(callerType)) {
            nonExistentTypeError(callerType);
        } else if (!methodReturnTypes.get(callerType).containsKey(node.getName())) {
            showError(String.format("type '%s' does not have method '%s'", callerType, node.getName()));
        }

        ArrayList<String> argTypes = methodArgTypes.get(callerType).get(node.getName());
        if (node.getArgs().size() != argTypes.size()) {
            showError(String.format("number of args does not match required number for method '%s'", node.getName()));
        }
        int count = 1;
        for (ASTExpr a : node.getArgs()) {
            a.accept(this);
            String argType = typeStack.pop();
            if (!argType.equals(argTypes.get(count - 1))) {
                showError(String.format("arg does not match type of arg %d in method '%s'", count, node.getName()));
            }
            count++;
        }
        typeStack.push(methodReturnTypes.get(callerType).get(node.getName()));
    }

    @Override
    public void visit(ThisExpr node) {
        typeStack.push(varTypes.get("this"));
    }

    @Override
    public void visit(FieldExpr node) {
        node.getCaller().accept(this);
        String callerType = typeStack.pop();
        if (!classes.contains(callerType)) {
            nonExistentTypeError(callerType);
        } else if (!fieldTypes.get(callerType).containsKey(node.getName())) {
            showError(String.format("type '%s' does not have field '%s'", callerType, node.getName()));
        }
        typeStack.push(fieldTypes.get(callerType).get(node.getName()));
    }

    @Override
    public void visit(ClassExpr node) {
        if (!classes.contains(node.getName())) {
            nonExistentTypeError(node.getName());
        } else if (node.getName().equals("int")) {
            showError("cannot instantiate class of type 'int'");
        }
        typeStack.push(node.getName());
    }

    @Override
    public void visit(ArithExpr node) {
        node.getExpr1().accept(this);
        checkArithType();
        node.getExpr2().accept(this);
        checkArithType();
        typeStack.push("int");
    }

    private void checkArithType() {
        String type = typeStack.pop();
        if (!type.equals("int")) {
            showError("invalid arithmetic operation with non-integer types");
        }
    }

    @Override
    public void visit(NullExpr node) {
        typeStack.push(node.getType());
    }

    private void nonExistentTypeError(String type) {
        showError(String.format("class '%s' does not exist", type));
    }

    private void showError(String err) {
        System.out.println("Error: " + err);
        System.exit(1);
    }
}
