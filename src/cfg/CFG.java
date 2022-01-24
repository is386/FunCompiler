package cfg;

import java.util.ArrayList;

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
}
