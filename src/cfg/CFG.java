package cfg;

import java.util.ArrayList;
import java.util.HashSet;

import cfg.primitives.TempPrimitive;
import cfg.stmt.IRStmt;

public class CFG {
    private int numFuncs = 0;
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private BasicBlock dataBlock = new BasicBlock("data", 0);
    private ArrayList<HashSet<String>> allVars = new ArrayList<>();
    private ArrayList<HashSet<String>> allBlocks = new ArrayList<>();
    private HashSet<String> vars = new HashSet<>();
    private HashSet<String> funcBlocks = new HashSet<>();
    private int tempVarCount = 1;

    public CFG() {
    }

    public HashSet<String> getFuncBlocks(int funcNum) {
        return allBlocks.get(funcNum);
    }

    public HashSet<String> getFuncVars(int funcNum) {
        return allVars.get(funcNum);
    }

    public void incrNumFuncs() {
        numFuncs += 1;
    }

    public int getNumFuncs() {
        return numFuncs;
    }

    public void addVar(String v) {
        vars.add(v);
    }

    public void resetVars() {
        allVars.add(vars);
        vars = new HashSet<>();
    }

    public void resetFuncBlocks() {
        allBlocks.add(funcBlocks);
        funcBlocks = new HashSet<>();
    }

    public void addToDataBlock(IRStmt ir) {
        dataBlock.push(ir);
    }

    public void setDataBlock(BasicBlock b) {
        dataBlock = b;
    }

    public void add(BasicBlock b) {
        blocks.add(b);
        funcBlocks.add(b.getName());
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    public TempPrimitive getNextTemp() {
        String varNum = Integer.toString(tempVarCount++);
        return new TempPrimitive(varNum);
    }

    public void resetTempVarCount() {
        tempVarCount = 1;
    }

    public void decrementTempVarCount() {
        tempVarCount = (tempVarCount == 1) ? tempVarCount : tempVarCount - 1;
    }

    public String toString() {
        String s = dataBlock.toString();
        s += "\ncode:\n";
        for (BasicBlock b : blocks) {
            s += "\n" + b;
        }
        return s;
    }
}
