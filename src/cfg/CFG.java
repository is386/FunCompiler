package cfg;

import java.util.ArrayList;
import java.util.HashSet;

import cfg.stmt.ControlStmt;

public class CFG {
    public static void buildGraph(ArrayList<BasicBlock> blocks) {
        for (BasicBlock parent : blocks) {
            ControlStmt c = parent.getControlStmt();
            if (c != null) {
                ArrayList<String> children = c.getBranchNames();
                for (BasicBlock child : blocks) {
                    if (children.contains(child.getName())) {
                        parent.addChild(child);
                        child.addParent(parent);
                    }
                }
            }
        }
    }

    public static void removeUnreachable(ArrayList<BasicBlock> blocks) {
        HashSet<BasicBlock> unreachable = new HashSet<>();
        for (BasicBlock b : blocks) {
            if (b.unreachable()) {
                unreachable.add(b);
            }
        }
        for (BasicBlock b : unreachable) {
            blocks.remove(b);
        }
    }
}
