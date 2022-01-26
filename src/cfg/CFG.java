package cfg;

import java.util.ArrayList;

import cfg.primitives.TempPrimitive;
import cfg.stmt.IRStmt;

public class CFG {
    private ArrayList<BasicBlock> blocks = new ArrayList<>();
    private BasicBlock dataBlock = new BasicBlock("data");
    private int tempVarCount = 1;

    public CFG() {
    }

    public void addToDataBlock(IRStmt ir) {
        dataBlock.push(ir);
    }

    public void setDataBlock(BasicBlock b) {
        dataBlock = b;
    }

    public void add(BasicBlock b) {
        blocks.add(b);
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
